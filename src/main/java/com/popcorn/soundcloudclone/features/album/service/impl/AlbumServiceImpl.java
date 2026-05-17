package com.popcorn.soundcloudclone.features.album.service.impl;

import com.popcorn.soundcloudclone.features.album.entity.Album;
import com.popcorn.soundcloudclone.features.album.entity.AlbumTrack;
import com.popcorn.soundcloudclone.features.album.repository.AlbumRepository;
import com.popcorn.soundcloudclone.features.album.repository.AlbumTrackRepository;
import com.popcorn.soundcloudclone.features.track.entity.Track;
import com.popcorn.soundcloudclone.features.track.repository.TrackRepository;
import com.popcorn.soundcloudclone.features.user.service.FavoriteAlbumService;
import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.album.dto.request.AlbumCreationRequest;
import com.popcorn.soundcloudclone.features.album.dto.request.AlbumFilterRequestDto;
import com.popcorn.soundcloudclone.features.album.dto.response.AlbumResponse;
import com.popcorn.soundcloudclone.features.album.dto.request.AlbumUpdateRequest;
import com.popcorn.soundcloudclone.features.album.repository.specification.AlbumSpecification;
import com.popcorn.soundcloudclone.features.album.service.AlbumService;
import com.popcorn.soundcloudclone.features.media.service.UploadService;
import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.common.exception.ErrorCode;
import com.popcorn.soundcloudclone.features.album.mapper.AlbumMapper;

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

    private final UploadService uploadService;
    private final FavoriteAlbumService favoriteService;

    private final String ALBUM_FOLDER = "soundcloud/album";

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
        var existing = album.getCoverImageUrl();

        if (upload != null && existing != null) {
            uploadService.upsert(upload, existing, ALBUM_FOLDER + "/" + album.getId(), "image");
        } else if (upload != null) {
            var newImage = uploadService.upload(upload, ALBUM_FOLDER + "/" + album.getId(), "image");
            album.setCoverImageUrl(newImage);
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
        var coverImage = album.getCoverImageUrl();
        if (coverImage != null) {
            uploadService.delete(coverImage);
        }

        albumTrackRepository.deleteByAlbumId(id);
        albumTrackRepository.flush();
        albumRepository.delete(album);
    }
}
