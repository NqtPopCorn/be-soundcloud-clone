package com.popcorn.soundcloudclone.domain.service;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.track.*;
import org.springframework.data.domain.Pageable;

public interface TrackService {
    TrackResponse createTrack(int userId, TrackCreationRequest request);

    TrackResponse updateTrack(int trackId, TrackUpdateRequest request);

    void deleteTrack(int trackId);

    PageResponse<TrackResponse> getPage(TrackQueryRequest filterReq, Pageable pageable);

    TrackResponse getTrackResponse(int trackId);
    // TrackResponse getTrackResponse(int trackId, int userId);

    String getAudioFilePath(int trackId);

    void increasePlayCount(int trackId, int userId);

    PageResponse<TrackResponse> getPageRecentPlay(Integer userId, Pageable pageable);
}
