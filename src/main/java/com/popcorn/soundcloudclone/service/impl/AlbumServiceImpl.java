package com.popcorn.soundcloudclone.service.impl;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumCreationRequest;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumUpdateRequest;
import com.popcorn.soundcloudclone.domain.entity.*;
import com.popcorn.soundcloudclone.domain.mapper.AlbumMapper;
import com.popcorn.soundcloudclone.repository.*;
import com.popcorn.soundcloudclone.repository.specification.AlbumSpecification;
import com.popcorn.soundcloudclone.service.AlbumService;
import com.popcorn.soundcloudclone.service.FavoriteService;
import com.popcorn.soundcloudclone.service.FileUploadService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final AlbumTrackRepository albumTrackRepository;
    private final TrackLikeRepository trackLikeRepository;
    private final AlbumMapper albumMapper;
//    private final PageResponseMapper<AlbumResponse> pageResponseMapper;

    private final FileUploadService fileUploadService;
    private final FavoriteService favoriteService;


    @Override
    public AlbumResponse getById(int albumId, Integer userId) { // lay tat ca
        var album = findAlbumByIdOrThrow(albumId);

        return albumMapper. toAlbumResponse(album, getLikedTracks(userId));
    }

    @Override
    public PageResponse<AlbumResponse> findByKeyword(String keyword, int page, int size, boolean asc, Integer userId) {
        Pageable pageable = PageRequest.of(page, size, asc? Sort.Direction.ASC : Sort.Direction.DESC, "id");
        var spec = AlbumSpecification.keywordContains(keyword);
        var albums = albumRepository.findAll(spec, pageable);
        return PageResponse.from(albums.map(
                album -> albumMapper.toAlbumResponse(album, getLikedTracks(userId))
        ));
    }

    @Override
    public AlbumResponse create(int userId, AlbumCreationRequest request) {
        var user = findUserByIdOrThrow(userId);
        Album album = Album.builder()
                .name(request.getName())
                .artist(user)
                .releaseDate(LocalDate.now())
                .build();
        return albumMapper.toAlbumResponse(albumRepository.save(album), getLikedTracks(userId));
    }

    private User findUserByIdOrThrow(int id) {
        return userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("User not found"));
    }

    private Album findAlbumByIdOrThrow(int id) {
        return albumRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Album not found"));
    }

    @Override
    @Transactional
    public void updateAlbum(int id, AlbumUpdateRequest request) {
        var album = findAlbumByIdOrThrow(id);
        album.setName(request.getName());
        album.setReleaseDate(request.getReleaseDate());
        var upload = request.getCoverImage();
        var existing = album.getCoverUpload();

        if (upload != null && existing != null) {
            fileUploadService.replaceFile(existing, upload);
        } else if (upload != null) {
            var newImage = fileUploadService.storeFile(upload, FileUpload.FileType.image);
            album.setCoverUpload(newImage);
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
            var track = trackRepository.findById(trackId).orElseThrow(()->new RuntimeException("Track not found"));
            AlbumTrack albumTrack = new AlbumTrack();
            albumTrack.setAlbum(album);
            albumTrack.setTrack(track);
            albumTrack.setPosition(i+1);
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
    public void deleteAlbumImage(int albumId) {
        var album = findAlbumByIdOrThrow(albumId);
        if (album.getCoverUpload() != null) {
            fileUploadService.deleteFile(album.getCoverUpload().getId());
        }
        album.setCoverUpload(null);
//        albumRepository.save(album);// ? khong can
    }

    @Override
    @Transactional
    public void deleteAlbum(int id) {
        var album = findAlbumByIdOrThrow(id);
        var coverImage = album.getCoverUpload();
        if (coverImage != null) {
            fileUploadService.deleteFile(coverImage.getId());
        }

        albumTrackRepository.deleteByAlbumId(id);
        albumTrackRepository.flush();
        albumRepository.delete(album);
    }

    private List<Integer> getLikedTracks(Integer userId) {
        if(userId == null) {
            return new ArrayList<>();
        }
        return favoriteService.getLikedTrackIds(userId);
    }
}
