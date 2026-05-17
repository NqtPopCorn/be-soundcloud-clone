package com.popcorn.soundcloudclone.features.auth.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class IntrospectRequest {
    String accessToken;
    // others token like: refresh token, csrf token, ex.
}
