package com.popcorn.soundcloudclone.features.auth.service.impl;

import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.common.exception.ErrorCode;
import com.popcorn.soundcloudclone.features.auth.dto.JwtPayload;
import com.popcorn.soundcloudclone.features.auth.service.JwtService;
import com.popcorn.soundcloudclone.features.users.entity.User;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtServiceImpl implements JwtService {
    @NonFinal
    @Value("${jwt.secret_key}")
    protected String JWT_SECRET_KEY;

    @Override
    public JwtPayload extractToken(String accessToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(accessToken);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            Date expirationDate = claimsSet.getExpirationTime();
            int userId = Integer.parseInt(claimsSet.getSubject());
            String username = claimsSet.getStringClaim("username");
            String authorities = claimsSet.getStringClaim("authorities");

            return JwtPayload.builder()
                    .userId(userId)
                    .username(username)
                    .authorities(authorities)
                    .expires(expirationDate.getTime())
                    .issuer(claimsSet.getIssuer())
                    .build();
        } catch (Exception e) {
            log.error("Cannot verify JWT", e);
            return null;
        }
    }

    @Override
    public boolean verifyToken(String accessToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(accessToken);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            Date expirationDate = claimsSet.getExpirationTime();
            Date currentDate = new Date();
            return currentDate.before(expirationDate);
        } catch (Exception e) {
            log.error("Cannot verify JWT", e);
            return false;
        }
    }

    /**
     * Create a jwt token with subject: userId and single role as authorities.
     * 
     * @return jwt token
     */
    @Override
    public String generateToken(JwtPayload jwtPayload) {
        // Hashing algorithm
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);

        // Body jwtPayload
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(String.valueOf(jwtPayload.getUserId()))
                .claim("username", jwtPayload.getUsername())
                .claim("authorities", "ROLE_" + jwtPayload.getAuthorities())
                .issuer(jwtPayload.getIssuer())
                .issueTime(new Date())
                .expirationTime(new Date(jwtPayload.getExpires()))
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
