package com.popcorn.soundcloudclone.domain.service;

import com.popcorn.soundcloudclone.domain.dto.auth.JwtVerifyResponse;

public interface JwtService {
    String generateToken(String username, String role, long expires);

    /**
     * Verify access token
     * 
     * @param accessToken jwt token
     * @return verify result and user info
     */
    JwtVerifyResponse verifyToken(String accessToken);
}
