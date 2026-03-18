package com.popcorn.soundcloudclone.domain.service;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.playlist.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlaylistService {
    PlaylistResponse getById(int id, int userId);

    PageResponse<PlaylistResponse> findByFilter(PlaylistFilterRequest dto, Pageable pageable);

    PageResponse<PlaylistSummaryResponse> findSummaries(PlaylistFilterRequest dto, Pageable pageable);

    List<PlaylistSummaryResponse> getUserPlaylistSummaries(int userId);

    PlaylistResponse create(int userId, PlaylistCreationRequest request);

    void patchUpdatePlaylist(int id, PlaylistUpdateRequest request);

    void updatePlaylistTracks(int id, List<Integer> trackIds);

    void addTracksToPlaylist(int playlistId, List<Integer> trackIds);

    void deletePlaylist(int id);

}
