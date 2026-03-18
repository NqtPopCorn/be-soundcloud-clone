package com.popcorn.soundcloudclone.domain.service.impl;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumCreationRequest;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumFilterRequestDto;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumUpdateRequest;
import com.popcorn.soundcloudclone.domain.entity.*;
import com.popcorn.soundcloudclone.domain.repository.*;
import com.popcorn.soundcloudclone.domain.repository.specification.AlbumSpecification;
import com.popcorn.soundcloudclone.domain.service.AlbumService;
import com.popcorn.soundcloudclone.domain.service.FavoriteAlbumService;
import com.popcorn.soundcloudclone.domain.service.FileUploadService;
import com.popcorn.soundcloudclone.exception.ApplicationException;
import com.popcorn.soundcloudclone.exception.ErrorCode;
import com.popcorn.soundcloudclone.mapper.AlbumMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final AlbumTrackRepository albumTrackRepository;
    // private final TrackLikeRepository trackLikeRepository;
    private final AlbumMapper albumMapper;
    // private final PageResponseMapper<AlbumResponse> pageResponseMapper;

    private final FileUploadService fileUploadService;
    private final FavoriteAlbumService favoriteService;

    @Override
    public AlbumResponse getById(int albumId, Integer userId) { // lay tat ca
        var album = findAlbumByIdOrThrow(albumId);

        return albumMapper.toAlbumResponse(album);
    }

    @Override
    public PageResponse<AlbumResponse> findByFilter(AlbumFilterRequestDto dto,
            Pageable pageable) {
        var spec = AlbumSpecification.keywordContains(dto.getKeyword())
                .and(AlbumSpecification.ownByArtistId(dto.getArtistId()));
        var albums = albumRepository.findAll(spec, pageable);
        return PageResponse.from(albums.map(
                albumMapper::toAlbumResponse));
    }

    @Override
    public AlbumResponse create(int userId, AlbumCreationRequest request) {
        var user = findUserByIdOrThrow(userId);
        Album album = Album.builder()
                .name(request.getName())
                .artist(user)
                .releaseDate(LocalDate.now())
                .build();
        return albumMapper.toAlbumResponse(albumRepository.save(album));
    }

    private User findUserByIdOrThrow(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
    }

    private Album findAlbumByIdOrThrow(int id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
    }

    @Override
    @Transactional
    public void updateAlbum(int id, AlbumUpdateRequest request) {
        var album = findAlbumByIdOrThrow(id);
        albumMapper.updateAlbum(album, request);
        var upload = request.getCoverImage();
        var existing = album.getCoverImage();

        if (upload != null && existing != null) {
            fileUploadService.replaceFile(existing, upload);
        } else if (upload != null) {
            var newImage = fileUploadService.storeFile(upload, FileUpload.FileType.image);
            album.setCoverImage(newImage);
        }

        albumRepository.save(album);
    }

    @Override
    @Transactional
    public void updateAlbumTracks(int albumId, List<Integer> trackIds) {
        Album album = findAlbumByIdOrThrow(albumId);

        // Xóa toàn bộ track cũ
        albumTrackRepository.deleteByAlbumId(albumId);

        // Chuẩn bị danh sách mới
        List<AlbumTrack> albumTracks = new ArrayList<>();
        for (int i = 0; i < trackIds.size(); i++) {
            int trackId = trackIds.get(i);
            var track = trackRepository.findById(trackId)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
            AlbumTrack albumTrack = new AlbumTrack();
            albumTrack.setAlbum(album);
            albumTrack.setTrack(track);
            albumTrack.setPosition(i + 1);
            albumTracks.add(albumTrack);
        }

        // Batch save
        albumTrackRepository.saveAll(albumTracks);
    }

    @Override
    @Transactional
    public void addTracksToAlbum(int albumId, List<Integer> trackIds) {
        var tracks = trackRepository.findByIdIn(trackIds);
        var album = findAlbumByIdOrThrow(albumId);
        for (Track track : tracks) {
            AlbumTrack albumTrack = AlbumTrack.builder()
                    .album(album)
                    .track(track)
                    .position(albumTrackRepository.countByAlbumId(albumId))
                    .build();
            albumTrackRepository.save(albumTrack);
        }

        albumTrackRepository.flush();
    }

    @Override
    @Transactional
    public void deleteAlbum(int id) {
        var album = findAlbumByIdOrThrow(id);
        var coverImage = album.getCoverImage();
        if (coverImage != null) {
            fileUploadService.deleteFile(coverImage.getId());
        }

        albumTrackRepository.deleteByAlbumId(id);
        albumTrackRepository.flush();
        albumRepository.delete(album);
    }
}
