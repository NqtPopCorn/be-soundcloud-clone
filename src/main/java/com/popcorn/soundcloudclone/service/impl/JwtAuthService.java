package com.popcorn.soundcloudclone.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.popcorn.soundcloudclone.domain.dto.auth.AuthResponse;
import com.popcorn.soundcloudclone.domain.dto.auth.JwtVerifyResponse;
import com.popcorn.soundcloudclone.domain.entity.User;
import com.popcorn.soundcloudclone.exception.BadRequestException;
import com.popcorn.soundcloudclone.exception.ErrorCode;
import com.popcorn.soundcloudclone.domain.mapper.UserMapper;
import com.popcorn.soundcloudclone.repository.UserRepository;
import com.popcorn.soundcloudclone.service.AuthService;
import com.popcorn.soundcloudclone.service.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtAuthService implements AuthService, JwtService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;

    @NonFinal
    @Value("${jwt.secret_key}")
    protected String JWT_SECRET_KEY;

//    @NonFinal
//    @Value("${jwt.refresh_key}")
//    protected String JWT_REFRESH_KEY;

    @Override
    public AuthResponse authenticate(String username, String password) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(()->new BadRequestException(ErrorCode.USER_NOT_FOUND));

        var authentivated = passwordEncoder.matches(password, user.getPassword());
        if(!authentivated) {
            throw new BadRequestException(ErrorCode.UNAUTHENTICATED);
        }

        String token = generateToken(username, user.getRole().name(), Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli());
        return AuthResponse.builder()
                .token(token)
                .user(userMapper.toUserResponse(user))
                .build();
    }

//    @Override
//    public AuthResponse refresh(String refreshToken) {
//        if(passwordEncoder.matches(refreshToken, JWT_REFRESH_KEY)) {
//
//        }
//    }

    @Override
    public JwtVerifyResponse verifyToken(String accessToken) {
        try {
            JWSVerifier verifier = new MACVerifier(JWT_SECRET_KEY);
            SignedJWT signedJWT = SignedJWT.parse(accessToken);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            Date expirationDate = claimsSet.getExpirationTime();

            boolean verified = signedJWT.verify(verifier); // kiem tra chu ki
            String username = claimsSet.getSubject();
            User found = userRepository.findByUsername(username).orElse(null);
            String authorities = claimsSet.getStringClaim("authorities");
            int userId = found != null ? found.getId() : 0;
            String message = "";
            int statusCode = 200;
            if(!verified) {
                message = "You are not authorized to perform this action"; //401
                statusCode = 401;
            } else if(!expirationDate.after(new Date())) {
                message = "Token expired"; // 403
                statusCode = 401;
            } else if(found == null) {
                message = "User not found"; //401
                statusCode = 401;
            }

            return JwtVerifyResponse.builder()
                    .valid(verified && expirationDate.after(new Date()) && found != null)
                    .authorities(authorities)
                    .username(username)
                    .userId(userId)
                    .statusCode(statusCode)
                    .errorMessage(message)
                    .build();
        } catch (JOSEException | ParseException e) {
            log.error("JWS verifier error");
            return JwtVerifyResponse.builder()
                    .valid(false)
                    .build();
        }

    }

    /**
     * Create a jwt token with subject: username and single role as authorities.
     * @return jwt token
     */
    @Override
    public String generateToken(String username, String role, long expiresDate) {
        // Hashing algorithm
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);

        // Body payload
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("authorities", "ROLE_"+role)
                .issuer("SoundCloudClone") // host
                .issueTime(new Date())
                .expirationTime(new Date(expiresDate))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        // Sign token
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(JWT_SECRET_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot sign JWT", e);
            throw new RuntimeException(e);
        }
    }

}
