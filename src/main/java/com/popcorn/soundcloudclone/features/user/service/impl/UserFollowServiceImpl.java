package com.popcorn.soundcloudclone.features.user.service.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.common.exception.ErrorCode;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.user.entity.UserFollower;
import com.popcorn.soundcloudclone.features.user.repository.UserFollowerRepository;
import com.popcorn.soundcloudclone.features.user.service.UserFollowService;
import com.popcorn.soundcloudclone.features.users.dto.response.UserResponse;
import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.users.mapper.UserMapper;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserFollowServiceImpl implements UserFollowService {
    private final UserRepository userRepository;
    private final UserFollowerRepository userFollowerRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void followUser(int currentUserId, int targetUserId) {
        if (currentUserId == targetUserId) {
            return;
        }

        User currentUser = findUserByIdOrThrow(currentUserId);
        User targetUser = findUserByIdOrThrow(targetUserId);

        if (userFollowerRepository.existsByUserIdAndArtistId(currentUserId, targetUserId)) {
            return;
        }

        userFollowerRepository.save(UserFollower.builder()
                .user(currentUser)
                .artist(targetUser)
                .build());
        userFollowerRepository.flush();
        userRepository.incrementFollowersCount(targetUserId);
        userRepository.incrementFollowingCount(currentUserId);
    }

    @Override
    @Transactional
    public void unfollowUser(int currentUserId, int targetUserId) {
        if (currentUserId == targetUserId) {
            return;
        }

        findUserByIdOrThrow(currentUserId);
        findUserByIdOrThrow(targetUserId);

        var existingRelation = userFollowerRepository.findByUserIdAndArtistId(currentUserId, targetUserId);
        if (existingRelation.isEmpty()) {
            return;
        }

        userFollowerRepository.delete(existingRelation.get());
        userFollowerRepository.flush();
        userRepository.decrementFollowersCount(targetUserId);
        userRepository.decrementFollowingCount(currentUserId);
    }

    @Override
    public PageResponse<UserResponse> getFollowers(int targetUserId, Pageable pageable) {
        findUserByIdOrThrow(targetUserId);
        return PageResponse.from(userFollowerRepository.findByArtistId(targetUserId, pageable)
                .map(relation -> userMapper.toUserResponse(relation.getUser())));
    }

    @Override
    public PageResponse<UserResponse> getFollowingUsers(int targetUserId, Pageable pageable) {
        findUserByIdOrThrow(targetUserId);
        return PageResponse.from(userFollowerRepository.findByUserId(targetUserId, pageable)
                .map(relation -> userMapper.toUserResponse(relation.getArtist())));
    }

    private User findUserByIdOrThrow(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new ApplicationException("Not found user with id: " + userId, ErrorCode.USER_NOT_FOUND));
    }
}
