package com.popcorn.soundcloudclone.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(String username);

    /**
     * Extract username WITHOUT VERIFY sign or exp
     * */
    String extractUsername(String token);
}
