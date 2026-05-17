package com.popcorn.soundcloudclone.features.users.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Use for nested response
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserSummaryResponse {
    int id;
    String username, stageName;
    String avatarUrl;
    String city;
    String country;
    int followersCount;
    int followingCount;
}
