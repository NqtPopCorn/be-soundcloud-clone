package com.popcorn.soundcloudclone.features.track.service;

import com.popcorn.soundcloudclone.features.track.dto.request.TrackCreationRequest;
import com.popcorn.soundcloudclone.features.track.dto.request.TrackQueryRequest;
import com.popcorn.soundcloudclone.features.track.dto.request.TrackUpdateRequest;
import com.popcorn.soundcloudclone.features.track.dto.response.TrackResponse;

import com.popcorn.soundcloudclone.common.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface TrackService {
    TrackResponse createTrack(int userId, TrackCreationRequest request);

    TrackResponse updateTrack(int trackId, TrackUpdateRequest request);

    void deleteTrack(int trackId);

    PageResponse<TrackResponse> getPage(TrackQueryRequest filterReq, Pageable pageable);

    TrackResponse getTrackResponse(int trackId);
    // TrackResponse getTrackResponse(int trackId, int userId);

    // String getAudioFilePath(int trackId);

    void increasePlayCount(int trackId, int userId);

    PageResponse<TrackResponse> getPageRecentPlay(Integer userId, Pageable pageable);
}
