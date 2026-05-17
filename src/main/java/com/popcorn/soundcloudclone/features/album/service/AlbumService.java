package com.popcorn.soundcloudclone.features.album.service;

import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.album.dto.request.AlbumCreationRequest;
import com.popcorn.soundcloudclone.features.album.dto.request.AlbumFilterRequestDto;
import com.popcorn.soundcloudclone.features.album.dto.response.AlbumResponse;
import com.popcorn.soundcloudclone.features.album.dto.request.AlbumUpdateRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AlbumService {
    AlbumResponse getById(int albumId, Integer userId);

    PageResponse<AlbumResponse> findByFilter(AlbumFilterRequestDto dto, Pageable pageable);

    AlbumResponse create(int userId, AlbumCreationRequest request);

    void updateAlbum(int id, AlbumUpdateRequest request);

    void updateAlbumTracks(int id, List<Integer> trackIds);

    void addTracksToAlbum(int albumId, List<Integer> trackIds);

    void deleteAlbum(int id);

    // like
    // unlike

}
