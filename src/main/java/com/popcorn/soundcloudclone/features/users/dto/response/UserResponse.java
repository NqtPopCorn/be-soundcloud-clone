package com.popcorn.soundcloudclone.features.users.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.popcorn.soundcloudclone.features.users.entity.User;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * User full info
 */
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class UserResponse {
    int id;

    String username;

    String email;

    String avatarUrl, backgroundUrl;

    String firstName, lastName, stageName, city, country, bio;

    int followersCount;
    int followingCount;

    LocalDate createdAt;

    String role;

}
