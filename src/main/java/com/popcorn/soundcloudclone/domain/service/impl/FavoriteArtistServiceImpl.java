package com.popcorn.soundcloudclone.domain.service.impl;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.user.UserResponse;
import com.popcorn.soundcloudclone.domain.dto.user.UserSummaryResponse;
import com.popcorn.soundcloudclone.domain.entity.User;
import com.popcorn.soundcloudclone.domain.entity.UserFollower;
import com.popcorn.soundcloudclone.domain.repository.UserFollowerRepository;
import com.popcorn.soundcloudclone.domain.repository.UserRepository;
import com.popcorn.soundcloudclone.domain.service.FavoriteArtistService;
import com.popcorn.soundcloudclone.exception.ApplicationException;
import com.popcorn.soundcloudclone.exception.ErrorCode;
import com.popcorn.soundcloudclone.mapper.UserMapper;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FavoriteArtistServiceImpl implements FavoriteArtistService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserFollowerRepository userFollowerRepository;

    @Override
    public PageResponse<UserSummaryResponse> getFavoriteArtists(Integer userId, Pageable pageable) {
        findUserByIdOrThrow(userId);
        return PageResponse.from(userFollowerRepository.findByUserId(userId, pageable).map(
                t -> userMapper.toUserSummaryResponse(t.getArtist())));
    }

    @Override
    @Transactional
    public void followArtist(Integer artistId, Integer followerId) {
        User artist = findUserByIdOrThrow(artistId);
        if (!userFollowerRepository.existsByUserIdAndArtistId(followerId, artistId)) {
            User follower = findUserByIdOrThrow(followerId);
            UserFollower userFollower = UserFollower.builder()
                    .user(follower)
                    .artist(artist)
                    .build();
            userFollowerRepository.save(userFollower);
            artist.setFollowersCount(artist.getFollowersCount() + 1);
            follower.setFollowersCount(follower.getFollowersCount() + 1);
            userRepository.flush();
        }
    }

    @Override
    @Transactional
    public void unfollowArtist(Integer artistId, Integer followerId) {
        findUserByIdOrThrow(artistId);
        userFollowerRepository.deleteByUserIdAndArtistId(followerId, artistId);
    }

    private User findUserByIdOrThrow(Integer userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ApplicationException("Not found user with id: " + userId, ErrorCode.USER_NOT_FOUND));
    }
}
