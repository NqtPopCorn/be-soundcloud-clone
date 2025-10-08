package com.popcorn.soundcloudclone.service.impl;

import com.popcorn.soundcloudclone.domain.dto.track.ArtistTrackResponse;
import com.popcorn.soundcloudclone.domain.dto.track.TrackResponse;
import com.popcorn.soundcloudclone.domain.dto.track.TrackCreationRequest;
import com.popcorn.soundcloudclone.domain.dto.track.TrackUpdateRequest;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.entity.*;
import com.popcorn.soundcloudclone.domain.mapper.PageResponseBuilder;
import com.popcorn.soundcloudclone.domain.mapper.TrackMapper;
import com.popcorn.soundcloudclone.repository.*;
import com.popcorn.soundcloudclone.repository.specification.TrackSpecification;
import com.popcorn.soundcloudclone.service.FileUploadService;
import com.popcorn.soundcloudclone.service.PlayCache;
import com.popcorn.soundcloudclone.service.TrackService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TrackServiceImpl implements TrackService {
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final FileUploadService fileUploadService;
    private final TrackMapper trackMapper;
    private final PageResponseBuilder<TrackResponse> pageResponseBuilder;
    private final TrackPlayRepository trackPlayRepository;
    private final TrackLikeRepository trackLikeRepository;
    private final PlayCache playCache;

    @Override
    @Transactional
    public ArtistTrackResponse createTrack(int userId, TrackCreationRequest request) {
        //goi mapper
        Track track = trackMapper.toTrack(request);

        track.setArtist(findUserOrThrow(userId));
        track.setGenres(genreRepository.findByIdIn(request.getGenreIds()));

        // luu file vao bo nho
        var avatar = fileUploadService.storeFile(request.getAudioUpload(), FileUpload.FileType.audio);
        track.setAudioUpload(avatar);
        var image = fileUploadService.storeFile(request.getImageUpload(), FileUpload.FileType.image);
        track.setImageUpload(image);

        return trackMapper.toArtistTrackResponse(trackRepository.save(track));

    }

    @Override
    public PageResponse<TrackResponse> getPage(
            String keyword,
            String artistUsername,
            Track.Privacy privacy,
            Pageable pageable) {
        Specification<Track> mainSpec = TrackSpecification.keywordContains(keyword)
                .and(TrackSpecification.hasPrivacy(privacy))
                .and(TrackSpecification.hasArtist(artistUsername));

        return pageResponseBuilder.toPageResponse(
                trackRepository.findAll(mainSpec, pageable)
                        .map(trackMapper::toTrackResponse)
        );
    }

    @Override
    public PageResponse<TrackResponse> getPage(
            int currentUserId,
            String keyword,
            String artistUsername,
            Track.Privacy privacy,
            Pageable pageable
    ) {
        // Các Specification đã kiểm tra null
        Specification<Track> mainSpec = TrackSpecification.keywordContains(keyword)
                .and(TrackSpecification.hasPrivacy(privacy))
                .and(TrackSpecification.hasArtist(artistUsername));

        List<Integer> likedTrackIds = getLikedTracks(currentUserId);

        return pageResponseBuilder.toPageResponse(
                trackRepository.findAll(mainSpec, pageable)
                        .map(track -> trackMapper.toTrackResponse(track, likedTrackIds))
        );
    }

    @Override
    public PageResponse<ArtistTrackResponse> getArtistTrackPage(
            String keyword,
            String username,
            Track.Privacy privacy,
            Pageable pageable) {
        Specification<Track> mainSpec = TrackSpecification.keywordContains(keyword)
                .and(TrackSpecification.hasPrivacy(privacy))
                .and(TrackSpecification.hasArtist(username));

//        List<Integer> likedTrackIds = getLikedTracks();

        return new PageResponseBuilder<ArtistTrackResponse>().toPageResponse(
                trackRepository.findAll(mainSpec, pageable)
                        .map(trackMapper::toArtistTrackResponse)
        );
    }

    @Override
    public TrackResponse getTrackResponse(int trackId) {
        return trackMapper.toTrackResponse(findTrackByIdOrThrow(trackId));
    }

    @Override
    public TrackResponse getTrackResponse(int trackId, int userId) {
        return trackMapper.toTrackResponse(findTrackByIdOrThrow(trackId), getLikedTracks(userId));
    }

    public Track findTrackByIdOrThrow(int trackId) {
        return trackRepository.findById(trackId).orElseThrow(()-> new RuntimeException("Track not found"));
    }

    private User findUserOrThrow(int userId) {
        return userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
    }

    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                UserDetails user = (UserDetails)authentication.getPrincipal();
                String username = user.getUsername();

                return userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("User not found"));
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean updateTrack(int id, TrackUpdateRequest request) {
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

        trackRepository.save(track);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteTrack(int trackId) {
        var track = trackRepository.findById(trackId).orElseThrow(()->new RuntimeException("Track not found"));
        trackRepository.delete(track);

        fileUploadService.deleteFile(track.getAudioUpload().getId());
        fileUploadService.deleteFile(track.getImageUpload().getId());
        return true;
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
