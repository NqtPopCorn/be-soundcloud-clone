package com.popcorn.soundcloudclone.domain.service;

import com.popcorn.soundcloudclone.domain.dto.auth.AuthResponse;
import com.popcorn.soundcloudclone.domain.dto.auth.JwtVerifyResponse;

public interface AuthService {

    /**
     * Check user info and create token (alt name: login)
     * 
     * @return jwtToken and user info
     */
    AuthResponse authenticate(String username, String password);

    // AuthResponse refresh(String refreshToken);
}
