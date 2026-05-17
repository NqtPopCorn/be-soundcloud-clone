package com.popcorn.soundcloudclone.features.user.service;

import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.track.dto.response.TrackResponse;

import java.util.Set;

import org.springframework.data.domain.Pageable;

public interface FavoriteTrackService {
    Set<Integer> getLikedTrackIds(Integer userId);

    PageResponse<TrackResponse> getLikedTracks(Integer userId, Pageable pageable);

    void toggleLikeTrack(int userId, int trackId, boolean liked);
}
