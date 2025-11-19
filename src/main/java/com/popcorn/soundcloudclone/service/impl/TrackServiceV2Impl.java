package com.popcorn.soundcloudclone.service.impl;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.track.*;
import com.popcorn.soundcloudclone.domain.entity.*;
import com.popcorn.soundcloudclone.domain.mapper.TrackMapper;
import com.popcorn.soundcloudclone.repository.*;
import com.popcorn.soundcloudclone.repository.specification.TrackSpecification;
import com.popcorn.soundcloudclone.security.UserSecurity;
import com.popcorn.soundcloudclone.service.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TrackServiceV2Impl implements TrackServiceV2 {
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final FileUploadService fileUploadService;
    private final TrackMapper trackMapper;
//    private final PageResponseMapper<TrackResponse> pageResponseMapper;
    private final TrackPlayRepository trackPlayRepository;
    private final TrackLikeRepository trackLikeRepository;
    private final PlayCache playCache;
    private final FavoriteService favoriteService;

    @Override
    @Transactional
    public TrackResponse createTrack(int userId, TrackCreationRequest request) {
        //goi mapper
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
    public PageResponse<TrackResponse> getPageForUser(
            TrackFilterRequest filterReq,
            Pageable pageable,
            Integer userId,
            Track.Privacy privacy) {
        Specification<Track> mainSpec = TrackSpecification
                .keywordContains(filterReq.getKeyword())
                .and(TrackSpecification.privacy(privacy))
                .and(TrackSpecification.hasArtistName(filterReq.getArtistName()));


        if(userId == null) {
            return PageResponse.from(trackRepository.findAll(mainSpec, pageable)
                    .map(trackMapper::toTrackResponse));
        }

        List<Integer> userLikedTrackIds = favoriteService.getLikedTrackIds(userId);
        return PageResponse.from(trackRepository.findAll(mainSpec, pageable)
                .map((track) -> trackMapper.toTrackResponse(track, userLikedTrackIds)));
    }

    @Override
    public TrackResponse getTrackResponse(int trackId) {
        return trackMapper.toTrackResponse(findTrackByIdOrThrow(trackId));
    }

    @Override
    public TrackResponse getTrackResponseForUser(int trackId, int userId) {
        Track track = findTrackByIdOrThrow(trackId);
        List<Integer> userLikedTrackIds = favoriteService.getLikedTrackIds(userId);
        return trackMapper.toTrackResponse(track, userLikedTrackIds);
    }

    public Track findTrackByIdOrThrow(int trackId) {
        return trackRepository.findById(trackId).orElseThrow(()-> new RuntimeException("Track not found"));
    }

    private User findUserOrThrow(int userId) {
        return userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
    }

    @Override
    @Transactional
    public TrackResponse updateTrack(int id, TrackUpdateRequest request) {
        Track track = findTrackByIdOrThrow(id);
        trackMapper.updateTrack(track, request);

        // update tags va file upload
        track.setGenres(genreRepository.findByIdIn(request.getGenreIds()));
        if(request.getAudioUpload() != null) {
            fileUploadService.replaceFile(track.getAudioUpload(), request.getAudioUpload());
        }
        if(request.getImageUpload() != null) {
            fileUploadService.replaceFile(track.getImageUpload(), request.getImageUpload());
        }

        return trackMapper.toTrackResponse(trackRepository.save(track));

    }

    @Override
    @Transactional
    public void deleteTrack(int trackId) {
        var track = trackRepository.findById(trackId).orElseThrow(()->new RuntimeException("Track not found"));
        trackRepository.delete(track);

        fileUploadService.deleteFile(track.getAudioUpload().getId());
        fileUploadService.deleteFile(track.getImageUpload().getId());
    }

    @Override
    public String getAudioFilePath(int trackId) {
        Track track = findTrackByIdOrThrow(trackId);
        if(track.getAudioUpload() == null) return null;
        return track.getAudioUpload().getFilePath();

    }

    @Override
    @Transactional
    public void increasePlayCount(int trackId, int userId) {
//        Track track = findTrackByIdOrThrow(trackId);
//        if(!trackPlaysRepository.existsByTrackIdAndUserId(trackId, userId)) {
//            TrackPlays log = TrackPlays.builder()
//                    .createdAt(LocalDateTime.now())
//                    .user(findUserOrThrow(userId))
//                    .track(track)
//                    .build();
//            trackPlaysRepository.save(log);
//            trackPlaysRepository.flush();
//            track.setPlayCount(track.getPlayCount() + 1);
//        }
        if (playCache.hasPlay(userId, trackId)) {
            return;
        }

        TrackPlay play = TrackPlay.builder()
                .userId(userId)
                .trackId(trackId)
                .createdAt(LocalDateTime.now())
                .build();
        trackPlayRepository.save(play);
        trackRepository.increasePlayCount(trackId);

        playCache.savePlay(userId, trackId, Duration.ofHours(1));
    }

    @Override
    @Transactional
    public void likeTrack(int trackId, int userId) {
        if(!trackLikeRepository.existsByTrackIdAndUserId(trackId, userId)) {
            // save to db
            Track track = findTrackByIdOrThrow(trackId);
            User user = findUserOrThrow(userId);
            TrackLike trackLike = TrackLike.builder()
                    .track(track)
                    .user(user)
                    .build();
            trackLikeRepository.save(trackLike);
            // increase like count for track
            trackLikeRepository.flush();
            track.setLikeCount(track.getLikeCount() + 1);
        }
    }

    @Override
    @Transactional
    public void unLikeTrack(int trackId, int userId) {
        TrackLike trackLike = trackLikeRepository.findByTrackIdAndUserId(trackId, userId).orElseThrow(()->new RuntimeException("Favorite not found"));
        trackLikeRepository.delete(trackLike);
        trackLikeRepository.flush();

        var track = findTrackByIdOrThrow(trackId);
        track.setLikeCount(track.getLikeCount() - 1);
    }

    private List<Integer> getLikedTracks(int userId) {
        return trackLikeRepository.getLikedTrackIds(userId).orElse(new ArrayList<>());
    }
}
