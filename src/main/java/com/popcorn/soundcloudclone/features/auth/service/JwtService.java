package com.popcorn.soundcloudclone.features.auth.service;

import com.popcorn.soundcloudclone.features.auth.dto.JwtPayload;

public interface JwtService {
    String generateToken(JwtPayload payload);

    boolean verifyToken(String accessToken);

    JwtPayload extractToken(String accessToken);
}
