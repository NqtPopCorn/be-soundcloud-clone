package com.popcorn.soundcloudclone.features.auth.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.popcorn.soundcloudclone.features.auth.dto.JwtPayload;
import com.popcorn.soundcloudclone.features.auth.dto.response.AuthResponse;
import com.popcorn.soundcloudclone.features.auth.entity.Session;
import com.popcorn.soundcloudclone.features.auth.repository.SessionRepository;
import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.common.exception.ErrorCode;
import com.popcorn.soundcloudclone.features.auth.service.AuthService;
import com.popcorn.soundcloudclone.features.auth.service.JwtService;
import com.popcorn.soundcloudclone.features.users.dto.request.UserCreationRequest;
import com.popcorn.soundcloudclone.features.users.dto.response.UserResponse;
import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.users.mapper.UserMapper;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    static int REFRESH_TOKEN_EXPIRATION_DAYS = 30;
    static int ACCESS_TOKEN_EXPIRATION_MINUTES = 30;

    @Override
    public AuthResponse authenticate(String username, String password) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        var authenticated = passwordEncoder.matches(password, user.getPassword());
        if (!authenticated) {
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }
        if (!user.isActive()) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        Instant accessTokenExpires = Instant.now().plus(ACCESS_TOKEN_EXPIRATION_MINUTES, ChronoUnit.MINUTES);
        JwtPayload payload = JwtPayload.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .authorities(user.getRole().name())
                .expires(accessTokenExpires.toEpochMilli())
                .issuer("SoundCloudClone")
                .build();
        String accessToken = jwtService.generateToken(payload);

        long refreshTokenExpiresIn = REFRESH_TOKEN_EXPIRATION_DAYS * 24 * 60 * 60;
        Instant refreshTokenExpires = Instant.now().plus(refreshTokenExpiresIn, ChronoUnit.SECONDS);
        UUID refreshToken = UUID.randomUUID();
        String hashedRefreshToken = Sha512DigestUtils.shaHex(refreshToken.toString());

        // Remove old refresh token if exists
        Session session = sessionRepository.findByUserId(user.getId()).orElse(null);
        if (session == null) {
            session = Session.builder()
                    .user(user)
                    .refreshTokenHash(hashedRefreshToken)
                    .expiresAt(refreshTokenExpires)
                    .build();
            sessionRepository.save(session);
        } else {
            session.setRefreshTokenHash(hashedRefreshToken);
            session.setExpiresAt(refreshTokenExpires);
            sessionRepository.save(session);
        }

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.toString())
                .refreshTokenExpiresIn(refreshTokenExpiresIn)
                .user(userMapper.toUserResponse(user))
                .build();
    }

    @Override
    public UserResponse register(UserCreationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApplicationException(ErrorCode.DUPLICATED_USER);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .active(true)
                .build();

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public void logout(String token) {
        String hashedRefreshToken = Sha512DigestUtils.shaHex(token);
        Session session = sessionRepository.findByRefreshTokenHash(hashedRefreshToken)
                .orElseThrow(() -> new ApplicationException(ErrorCode.FORBIDDEN));
        sessionRepository.delete(session);
    }

    @Override
    public String refreshToken(String token) {
        String hashedRefreshToken = Sha512DigestUtils.shaHex(token);
        Session session = sessionRepository.findByRefreshTokenHash(hashedRefreshToken)
                .orElseThrow(() -> new ApplicationException(ErrorCode.FORBIDDEN));

        if (session.getExpiresAt().isBefore(Instant.now())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        Instant newAccessTokenExpires = Instant.now().plus(ACCESS_TOKEN_EXPIRATION_MINUTES, ChronoUnit.MINUTES);

        return jwtService.generateToken(JwtPayload.builder()
                .userId(session.getUser().getId())
                .username(session.getUser().getUsername())
                .authorities(session.getUser().getRole().name())
                .expires(newAccessTokenExpires.toEpochMilli())
                .issuer("SoundCloudClone")
                .build());
    }

}
