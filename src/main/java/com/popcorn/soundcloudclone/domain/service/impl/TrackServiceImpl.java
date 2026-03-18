package com.popcorn.soundcloudclone.domain.service.impl;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.track.*;
import com.popcorn.soundcloudclone.domain.entity.*;
import com.popcorn.soundcloudclone.domain.repository.*;
import com.popcorn.soundcloudclone.domain.repository.specification.TrackSpecification;
import com.popcorn.soundcloudclone.domain.service.*;
import com.popcorn.soundcloudclone.mapper.TrackMapper;
import com.popcorn.soundcloudclone.security.CurrentUserContext;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService {
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final FileUploadService fileUploadService;
    private final TrackMapper trackMapper;
    // private final PageResponseMapper<TrackResponse> pageResponseMapper;
    private final TrackPlayRepository trackPlayRepository;
    private final TrackLikeRepository trackLikeRepository;
    // private final PlayCache playCache;
    private final CurrentUserContext currentUserContext;

    @Override
    @Transactional
    public TrackResponse createTrack(int userId, TrackCreationRequest request) {
        // goi mapper
        Track track = trackMapper.toTrack(request);

        track.setArtist(findUserOrThrow(userId));
        track.setGenres(genreRepository.findByIdIn(request.getGenreIds()));

        // luu file vao bo nho
        var avatar = fileUploadService.storeFile(request.getAudioUpload(), FileUpload.FileType.audio);
        track.setAudioUpload(avatar);
        var image = fileUploadService.storeFile(request.getImageUpload(), FileUpload.FileType.image);
        track.setImageUpload(image);

        return trackMapper.toTrackResponse(trackRepository.save(track));

    }

    @Override
    public PageResponse<TrackResponse> getPage(
            TrackQueryRequest filterReq,
            Pageable pageable) {

        Track.Privacy privacy = Track.Privacy.PUBLIC;
        if (filterReq.getArtistId() == currentUserContext.getCurrentUserId()) {
            privacy = null; // all privacy
        }
        Specification<Track> mainSpec = TrackSpecification
                .keywordContains(filterReq.getKeyword())
                .and(TrackSpecification.isInAlbum(filterReq.getAlbumId()))
                .and(TrackSpecification.isInPlaylist(filterReq.getPlaylistId()))
                .and(TrackSpecification.privacy(privacy))
                .and(TrackSpecification.hasArtistId(filterReq.getArtistId()))
                .and(TrackSpecification.hasGenre(filterReq.getGenre()));

        Integer userId = currentUserContext.getCurrentUserId();
        if (userId == null) {
            return PageResponse.from(trackRepository.findAll(mainSpec, pageable)
                    .map(trackMapper::toTrackResponse));
        }

        return PageResponse.from(trackRepository.findAll(mainSpec, pageable)
                .map(trackMapper::toTrackResponse));
    }

    @Override
    public TrackResponse getTrackResponse(int trackId) {
        return trackMapper.toTrackResponse(findTrackByIdOrThrow(trackId));
    }

    public Track findTrackByIdOrThrow(int trackId) {
        return trackRepository.findById(trackId).orElseThrow(() -> new RuntimeException("Track not found"));
    }

    private User findUserOrThrow(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional
    public TrackResponse updateTrack(int id, TrackUpdateRequest request) {
        Track track = findTrackByIdOrThrow(id);
        trackMapper.updateTrack(track, request);

        // // update tags va file upload
        if (request.getGenreIds() != null) {
            track.setGenres(genreRepository.findByIdIn(request.getGenreIds()));
        }
        if (request.getAudioUpload() != null) {
            fileUploadService.replaceFile(track.getAudioUpload(), request.getAudioUpload());
        }
        if (request.getImageUpload() != null) {
            fileUploadService.replaceFile(track.getImageUpload(), request.getImageUpload());
        }
        trackRepository.flush();

        return trackMapper.toTrackResponse(track);

    }

    @Override
    @Transactional
    public void deleteTrack(int trackId) {
        var track = trackRepository.findById(trackId).orElseThrow(() -> new RuntimeException("Track not found"));
        trackRepository.delete(track);
        fileUploadService.deleteFile(track.getAudioUpload().getId());
        fileUploadService.deleteFile(track.getImageUpload().getId());
    }

    @Override
    public String getAudioFilePath(int trackId) {
        Track track = findTrackByIdOrThrow(trackId);
        if (track.getAudioUpload() == null)
            return null;
        return track.getAudioUpload().getFilePath();

    }

    @Override
    @Transactional
    public void increasePlayCount(int trackId, int userId) {
        // if (playCache.hasPlay(userId, trackId)) {
        // return;
        // }

        TrackPlay play = TrackPlay.builder()
                .user(userRepository.getReferenceById(userId))
                .track(trackRepository.getReferenceById(trackId))
                .createdAt(LocalDateTime.now())
                .build();
        trackPlayRepository.save(play);
        trackRepository.increasePlayCount(trackId);

        // playCache.savePlay(userId, trackId, Duration.ofHours(1));
    }

    @Override
    public PageResponse<TrackResponse> getPageRecentPlay(Integer userId, Pageable pageable) {
        var pageResult = trackPlayRepository.findByUserId(userId, pageable);

        return PageResponse.from(
                pageResult.map(p -> trackMapper.toTrackResponse(p.getTrack())));
    }
}
