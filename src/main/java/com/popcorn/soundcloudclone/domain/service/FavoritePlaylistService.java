package com.popcorn.soundcloudclone.domain.service;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistSummaryResponse;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

// khong dung audit vi audit chi nen dung o low level logic con day la business logic
public interface FavoritePlaylistService {

    // cache this method?
    Set<Integer> getLikedPlaylistIds(Integer userId);

    PageResponse<PlaylistSummaryResponse> getLikedPlaylists(Integer userId, Pageable pageable);

    void likePlaylist(int userId, int playlistId);

    void unlikePlaylist(int userId, int playlistId);
}
