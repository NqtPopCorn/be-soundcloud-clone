//package com.popcorn.soundcloudclone.service.impl;
//
//import com.nimbusds.jose.*;
//import com.nimbusds.jose.crypto.MACSigner;
//import com.nimbusds.jose.crypto.MACVerifier;
//import com.nimbusds.jwt.JWTClaimsSet;
//import com.nimbusds.jwt.SignedJWT;
//import com.popcorn.soundcloudclone.domain.auth.dto.IntrospectResponse;
//import com.popcorn.soundcloudclone.service.JwtService;
//import lombok.experimental.NonFinal;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.text.ParseException;
//import java.util.Date;
//
//@Slf4j
//@Service
//public class JwtServiceImpl implements JwtService {
//    @NonFinal
//    @Value("${jwt.secret_key}")
//    protected String SECRET_KEY;
//
//    public String generateToken(String username) {
//        // Chua thuat toan hash
//        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
//
//        // Body payload
//        Date now = new Date();
//        Date exp = new Date(now.getTime() + 24 * 60 * 60 * 1000);
//        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
//                .subject(username)
//                .issuer("SoundCloudClone")
//                .issueTime(now)
//                .expirationTime(exp)
//                .build();
//
//        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
//
//        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
//
//        // Ky token
//        try {
//            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
//            return jwsObject.serialize();
//        } catch (JOSEException e) {
//            log.error("Cannot sign JWT", e);
//            throw new RuntimeException(e);
//        }
//    }
//
//    public String extractUsername(String token) {
//        try {
//            JWSVerifier verifier = new MACVerifier(SECRET_KEY);
//            SignedJWT signedJWT = SignedJWT.parse(token);
//            Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
//
//            boolean verified = signedJWT.verify(verifier);
//            Integer userId = Integer.parseInt(signedJWT.getJWTClaimsSet().getSubject());
//            boolean isUserExist = userRepository.existsById(userId);
//            String username = signedJWT.getJWTClaimsSet().getStringClaim("username");
//            String authorities = signedJWT.getJWTClaimsSet().getStringClaim("authorities");
//            String message = "";
//            if(!verified) {
//                message = "You are not authorized to perform this action";
//            } else if(!expirationDate.after(new Date())) {
//                message = "Token expired";
//            } else if(!isUserExist) {
//                message = "User not found";
//            }
//
//            return username;
//        } catch (JOSEException | ParseException e) {
//
//        }
//    }
//}
//
