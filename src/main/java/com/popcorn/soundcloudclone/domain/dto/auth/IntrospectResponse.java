package com.popcorn.soundcloudclone.domain.dto.auth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class IntrospectResponse {
    boolean valid;
    Integer userId;
    String username;
    String authorities;
    String message;

}
