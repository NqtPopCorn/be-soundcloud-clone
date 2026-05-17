package com.popcorn.soundcloudclone.features.user.service;

import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.album.dto.response.AlbumSummaryResponse;

import java.util.Set;

import org.springframework.data.domain.Pageable;

public interface FavoriteAlbumService {
    Set<Integer> getLikedAlbumIds(Integer userId);

    PageResponse<AlbumSummaryResponse> getLikedAlbums(Integer userId, Pageable pageable);

    void toggleLikeAlbum(int userId, int albumId, boolean liked);
}
