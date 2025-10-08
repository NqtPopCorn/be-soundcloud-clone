package com.popcorn.soundcloudclone.domain.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * User full info
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class UserResponse {
    String id;

    String username;

    String email;

    String avatarUrl, backgroundUrl;

    String firstName, lastName, stageName, city, country, bio;

    int followersCount;
    int followingCount;

    LocalDate createdAt;

    String role;

}
