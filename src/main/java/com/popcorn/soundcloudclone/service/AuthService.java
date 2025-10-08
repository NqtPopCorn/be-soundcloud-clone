package com.popcorn.soundcloudclone.service;

import com.popcorn.soundcloudclone.domain.dto.auth.AuthResponse;
import com.popcorn.soundcloudclone.domain.dto.auth.IntrospectResponse;

public interface AuthService {

    /**
     * Check user info and create token (alt name: login)
     * @return jwtToken and user info
     */
    AuthResponse authenticate(String username, String password);

    /**
     * Verify access token (alt name: verify)
     * @param accessToken jwt token
     * @return verify result and user info
     */
    IntrospectResponse introspect(String accessToken);
}
