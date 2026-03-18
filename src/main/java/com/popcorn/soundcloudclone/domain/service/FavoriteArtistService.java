package com.popcorn.soundcloudclone.domain.service;

import org.springframework.data.domain.Pageable;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.user.UserSummaryResponse;

public interface FavoriteArtistService {
    PageResponse<UserSummaryResponse> getFavoriteArtists(Integer userId, Pageable pageable);

    void followArtist(Integer artistId, Integer followerId);

    void unfollowArtist(Integer artistId, Integer followerId);
}
