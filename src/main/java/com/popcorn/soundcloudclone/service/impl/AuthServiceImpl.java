package com.popcorn.soundcloudclone.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.popcorn.soundcloudclone.domain.dto.auth.AuthResponse;
import com.popcorn.soundcloudclone.domain.dto.auth.IntrospectResponse;
import com.popcorn.soundcloudclone.domain.entity.User;
import com.popcorn.soundcloudclone.exception.BadRequestException;
import com.popcorn.soundcloudclone.exception.ErrorCode;
import com.popcorn.soundcloudclone.domain.mapper.UserMapper;
import com.popcorn.soundcloudclone.repository.UserRepository;
import com.popcorn.soundcloudclone.service.AuthService;
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
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;

    @NonFinal
    @Value("${jwt.secret_key}")
    protected String SECRET_KEY;

    public AuthResponse authenticate(String username, String password) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(()->new BadRequestException(ErrorCode.USER_NOT_FOUND));

        var authentivated = passwordEncoder.matches(password, user.getPassword());
        if(!authentivated) {
            throw new BadRequestException(ErrorCode.UNAUTHENTICATED);
        }

        String token = createToken(username, user.getRole().name(), Instant.now().plus(24, ChronoUnit.HOURS).toEpochMilli());
        return AuthResponse.builder()
                .token(token)
                .user(userMapper.toUserResponse(user))
                .build();
    }

    public IntrospectResponse introspect(String accessToken) {
        try {
            JWSVerifier verifier = new MACVerifier(SECRET_KEY);
            SignedJWT signedJWT = SignedJWT.parse(accessToken);
            Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();

            boolean verified = signedJWT.verify(verifier); // kiem tra chu ki
            String username = signedJWT.getJWTClaimsSet().getSubject();
            User found = userRepository.findByUsername(username).orElse(null);
            String authorities = signedJWT.getJWTClaimsSet().getStringClaim("authorities");
            String message = "";
            int userId = 0;
            if(found != null) {
                userId = found.getId();
            }
            if(!verified) {
                message = "You are not authorized to perform this action";
            } else if(!expirationDate.after(new Date())) {
                message = "Token expired";
            } else if(found == null) {
                message = "User not found";
            }

            return IntrospectResponse.builder()
                    .valid(verified && expirationDate.after(new Date()) && found != null)
                    .authorities(authorities)
                    .username(username)
                    .userId(userId)
                    .message(message)
                    .build();
        } catch (JOSEException | ParseException e) {
            log.error("JWS verifier error");
            return IntrospectResponse.builder()
                    .valid(false)
                    .build();
        }

    }

    /**
     * Create a jwt token with subject: username and single role as authorities.
     * @return jwt token
     */
    private String createToken(String username,String role, long expiresDate) {
        // Chua thuat toan hash
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);

        // Body payload
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("authorities", "ROLE_"+role)
//                .claim("userId", userId)
                .issuer("SoundCloudClone") // host
                .issueTime(new Date())
                .expirationTime(new Date(expiresDate))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        // Ky token
        try {
            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot sign JWT", e);
            throw new RuntimeException(e);
        }
    }

}
