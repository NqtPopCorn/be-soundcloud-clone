package com.popcorn.soundcloudclone.service;

import com.popcorn.soundcloudclone.domain.dto.track.ArtistTrackResponse;
import com.popcorn.soundcloudclone.domain.dto.track.TrackResponse;
import com.popcorn.soundcloudclone.domain.dto.track.TrackCreationRequest;
import com.popcorn.soundcloudclone.domain.dto.track.TrackUpdateRequest;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.entity.Track;
import org.springframework.data.domain.Pageable;

public interface TrackService {
    // create, update, delete, getPage
    // getByUserId(include private): cho user service lam ? nah
    ArtistTrackResponse createTrack(int userId, TrackCreationRequest request);
    boolean updateTrack(int trackId, TrackUpdateRequest request);
    boolean deleteTrack(int trackId);
    PageResponse<TrackResponse> getPage(
            String keyword,
            String artistUsername,
            Track.Privacy privacy,
            Pageable pageable
    );
    PageResponse<TrackResponse> getPage(
            int userId,
            String keyword,
            String artistUsername,
            Track.Privacy privacy,
            Pageable pageable
    );
    PageResponse<ArtistTrackResponse> getArtistTrackPage(
            String keyword,
            String username,
            Track.Privacy privacy,
            Pageable pageable
    );
    TrackResponse getTrackResponse(int trackId);
    TrackResponse getTrackResponse(int trackId, int userId);

    String getAudioFilePath(int trackId);
    void increasePlayCount(int trackId, int userId);
    void likeTrack(int trackId, int userId);
    void unLikeTrack(int trackId, int userId);
}
