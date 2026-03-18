package com.popcorn.soundcloudclone.domain.service;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.track.TrackResponse;

import java.util.Set;

import org.springframework.data.domain.Pageable;

public interface FavoriteTrackService {
    Set<Integer> getLikedTrackIds(Integer userId);

    PageResponse<TrackResponse> getLikedTracks(Integer userId, Pageable pageable);

    void likeTrack(int userId, int trackId);

    void unlikeTrack(int userId, int trackId);
}
