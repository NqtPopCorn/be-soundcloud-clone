package com.popcorn.soundcloudclone.service;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumCreationRequest;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumUpdateRequest;
import java.util.List;

public interface AlbumService {
    AlbumResponse getById(int id);

    PageResponse<AlbumResponse> findByKeyword(String keyword, int page, int size, boolean asc);

    AlbumResponse create(int userId, AlbumCreationRequest request);

    void updateAlbum(int id, AlbumUpdateRequest request);

    void updateAlbumTracks(int id, List<Integer> trackIds);

    void addTracksToAlbum(int albumId, List<Integer> trackIds);

    /**
     * Delete album image if image non null
     */
    void deleteAlbumImage(int albumId);

    void deleteAlbum(int id);

}
