package com.popcorn.soundcloudclone.service;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistCreationRequest;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistResponse;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistUpdateRequest;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistSummaryResponse;

import java.util.List;

public interface PlaylistService {
    PlaylistResponse getById(int id);
    
    PageResponse<PlaylistResponse> findByKeyword(String keyword, int page, int size, boolean asc);
    List<PlaylistSummaryResponse> getUserPlaylistSummaries(int userId);

    PlaylistResponse create(int userId, PlaylistCreationRequest request);
    
    void updatePlaylist(int id, PlaylistUpdateRequest request);
    
    void updatePlaylistTracks(int id, List<Integer> trackIds);
    
    void addTracksToPlaylist(int playlistId, List<Integer> trackIds);

    void deletePlaylist(int id);

}
