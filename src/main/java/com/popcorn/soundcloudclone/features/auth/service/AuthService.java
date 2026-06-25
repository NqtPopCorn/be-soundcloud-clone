package com.popcorn.soundcloudclone.features.auth.service;

import com.popcorn.soundcloudclone.features.auth.dto.response.AuthResponse;
import com.popcorn.soundcloudclone.features.users.dto.request.UserCreationRequest;
import com.popcorn.soundcloudclone.features.users.dto.response.UserResponse;

public interface AuthService {
    AuthResponse authenticate(String username, String password);

    UserResponse register(UserCreationRequest request);

    void logout(String refresh_token);

    String refreshToken(String token);

    AuthResponse authenticateWithGoogle(String credential);
}
