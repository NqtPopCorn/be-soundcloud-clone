package com.popcorn.soundcloudclone.features.user.service;

import org.springframework.data.domain.Pageable;

import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.users.dto.response.UserResponse;

public interface UserFollowService {
    void followUser(int currentUserId, int targetUserId);

    void unfollowUser(int currentUserId, int targetUserId);

    PageResponse<UserResponse> getFollowers(int targetUserId, Pageable pageable);

    PageResponse<UserResponse> getFollowingUsers(int targetUserId, Pageable pageable);
}
