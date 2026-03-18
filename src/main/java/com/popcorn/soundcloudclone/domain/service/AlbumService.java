package com.popcorn.soundcloudclone.domain.service;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumCreationRequest;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumFilterRequestDto;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumUpdateRequest;
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
