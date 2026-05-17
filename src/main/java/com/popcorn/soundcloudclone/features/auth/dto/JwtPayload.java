package com.popcorn.soundcloudclone.features.auth.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JwtPayload {
    private Integer userId;
    private String username;
    private String authorities;
    /**
     * Expiry date in milliseconds
     */
    private long expires;
    private String issuer;
}
