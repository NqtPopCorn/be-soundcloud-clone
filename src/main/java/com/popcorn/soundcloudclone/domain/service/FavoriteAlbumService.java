package com.popcorn.soundcloudclone.domain.service;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumSummaryResponse;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

public interface FavoriteAlbumService {
    Set<Integer> getLikedAlbumIds(Integer userId);

    PageResponse<AlbumSummaryResponse> getLikedAlbums(Integer userId, Pageable pageable);

    void likeAlbum(int userId, int albumId);

    void unlikeAlbum(int userId, int albumId);
}
