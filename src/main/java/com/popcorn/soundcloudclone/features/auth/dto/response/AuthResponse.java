package com.popcorn.soundcloudclone.features.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.popcorn.soundcloudclone.features.users.dto.response.UserResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@JsonIgnoreProperties({ "refreshTokenExpiresIn" })
public class AuthResponse {
    String accessToken;

    String refreshToken;

    long refreshTokenExpiresIn;

    UserResponse user;
}


