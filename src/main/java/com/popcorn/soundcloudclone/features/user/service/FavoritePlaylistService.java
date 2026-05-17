package com.popcorn.soundcloudclone.features.user.service;

import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.playlist.dto.response.PlaylistSummaryResponse;

import java.util.Set;

import org.springframework.data.domain.Pageable;

// khong dung audit vi audit chi nen dung o low level logic con day la business logic
public interface FavoritePlaylistService {

    // cache this method?
    Set<Integer> getLikedPlaylistIds(Integer userId);

    PageResponse<PlaylistSummaryResponse> getLikedPlaylists(Integer userId, Pageable pageable);

    void toggleLikePlaylist(int userId, int playlistId, boolean liked);

}
