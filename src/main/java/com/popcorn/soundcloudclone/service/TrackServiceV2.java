package com.popcorn.soundcloudclone.service;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.track.*;
import com.popcorn.soundcloudclone.domain.entity.Track;
import org.springframework.data.domain.Pageable;

public interface TrackServiceV2 {
    TrackResponse createTrack(int userId, TrackCreationRequest request);
    TrackResponse updateTrack(int trackId, TrackUpdateRequest request);
    void deleteTrack(int trackId);
    PageResponse<TrackResponse> getPageForUser(TrackFilterRequest filterReq, Pageable pageable, Integer userId, Track.Privacy privacy);
    TrackResponse getTrackResponse(int trackId);
    TrackResponse getTrackResponseForUser(int trackId, int userId);

    String getAudioFilePath(int trackId);
    void increasePlayCount(int trackId, int userId);
    void likeTrack(int trackId, int userId);
    void unLikeTrack(int trackId, int userId);
}
