package com.popcorn.soundcloudclone.domain.dto.auth;

import com.popcorn.soundcloudclone.domain.dto.user.UserResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
public class AuthResponse {
    String token;
    UserResponse user;
//    @JsonIgnore
//    String refreshToken;
}
