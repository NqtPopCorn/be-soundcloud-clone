package com.popcorn.soundcloudclone.features.track.service.impl;

import com.popcorn.soundcloudclone.features.genre.repository.GenreRepository;
import com.popcorn.soundcloudclone.features.media.service.UploadService;
import com.popcorn.soundcloudclone.features.track.dto.request.TrackCreationRequest;
import com.popcorn.soundcloudclone.features.track.dto.request.TrackQueryRequest;
import com.popcorn.soundcloudclone.features.track.dto.request.TrackUpdateRequest;
import com.popcorn.soundcloudclone.features.track.dto.response.TrackResponse;
import com.popcorn.soundcloudclone.features.track.entity.Track;
import com.popcorn.soundcloudclone.features.track.entity.TrackPlay;
import com.popcorn.soundcloudclone.features.track.repository.TrackLikeRepository;
import com.popcorn.soundcloudclone.features.track.repository.TrackPlayRepository;
import com.popcorn.soundcloudclone.features.track.repository.TrackRepository;
import com.popcorn.soundcloudclone.features.track.service.TrackService;
import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.track.repository.specification.TrackSpecification;
import com.popcorn.soundcloudclone.features.track.mapper.TrackMapper;
import com.popcorn.soundcloudclone.common.security.CurrentUserContext;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService {
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final UploadService uploadService;
    private final TrackMapper trackMapper;
    private final TrackPlayRepository trackPlayRepository;
    private final CurrentUserContext currentUserContext;
    private final StringRedisTemplate redisTemplate;

    private static final String VIEWED_KEY_PREFIX = "viewed:";
    private static final String PLAY_QUEUE_KEY = "track:plays:queue";
    private static final Duration DEDUP_TTL = Duration.ofMinutes(30);

    private final String TRACK_FOLDER = "soundcloud/track";

    @Override
    @Transactional
    public TrackResponse createTrack(int userId, TrackCreationRequest request) {
        Track track = trackMapper.toTrack(request);

        track.setArtist(findUserOrThrow(userId));
        track.setGenres(genreRepository.findByIdIn(request.getGenreIds()));
        trackRepository.save(track);

        // Upload to Cloudinary
        String audioUrl = uploadService.upload(request.getAudioUpload(), TRACK_FOLDER + "/" + track.getId(), "audio");
        if (audioUrl != null)
            track.setAudioUrl(audioUrl);
        String imageUrl = uploadService.upload(request.getImageUpload(), TRACK_FOLDER + "/" + track.getId(), "image");
        if (imageUrl != null)
            track.setImageUrl(imageUrl);
        trackRepository.save(track);

        return trackMapper.toTrackResponse(track);
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

        if (request.getGenreIds() != null) {
            track.setGenres(genreRepository.findByIdIn(request.getGenreIds()));
        }
        // Replace audio/image on Cloudinary (upload new version, overwrite URL)
        if (request.getAudioUpload() != null) {
            String audioUrl = uploadService.upload(request.getAudioUpload(), TRACK_FOLDER + "/" + track.getId(),
                    "audio");
            if (audioUrl != null)
                track.setAudioUrl(audioUrl);
        }
        if (request.getImageUpload() != null) {
            String imageUrl = uploadService.upload(request.getImageUpload(), TRACK_FOLDER + "/" + track.getId(),
                    "image");
            if (imageUrl != null)
                track.setImageUrl(imageUrl);
        }
        trackRepository.flush();

        return trackMapper.toTrackResponse(track);
    }

    @Override
    @Transactional
    public void deleteTrack(int trackId) {
        var track = trackRepository.findById(trackId).orElseThrow(() -> new RuntimeException("Track not found"));
        // Delete from Cloudinary before removing DB record
        if (track.getAudioUrl() != null) {
            uploadService.delete(track.getAudioUrl());
        }
        if (track.getImageUrl() != null) {
            uploadService.delete(track.getImageUrl());
        }
        trackRepository.delete(track);
    }

    /**
     * Redis-first play count: dedup via SETNX with 30-min TTL, save first then
     * queue the
     * counter increase event for the batch worker to persist to MySQL.
     */
    @Override
    public void increasePlayCount(int trackId, int userId) {
        String dedupKey = VIEWED_KEY_PREFIX + userId + ":" + trackId;

        // SETNX with TTL — returns true if the key was newly set (unique play within
        // window)
        Boolean isNewPlay = redisTemplate.opsForValue()
                .setIfAbsent(dedupKey, "1", DEDUP_TTL);

        // khong dung isNewPlay == true vì isNewPLay có thể null
        if (Boolean.TRUE.equals(isNewPlay)) {
            try {
                // Enqueue for the batch sync worker: "trackId:userId"
                redisTemplate.opsForList().rightPush(PLAY_QUEUE_KEY, trackId + ":" + userId);
                TrackPlay play = TrackPlay.builder()
                        .track(trackRepository.getReferenceById(trackId))
                        .user(userRepository.getReferenceById(userId))
                        .createdAt(LocalDateTime.now())
                        .build();
                trackPlayRepository.save(play);
                log.debug("Queued play event, saved history: trackId={}, userId={}", trackId, userId);
            } catch (Exception e) {
                log.warn("Failed to save TrackPlay log for trackId={}, userId={}: {}",
                        trackId, userId, e.getMessage());
            }

        } else {
            log.debug("Duplicate play ignored (within 30-min window): trackId={}, userId={}", trackId, userId);
        }
    }

    @Override
    public PageResponse<TrackResponse> getPageRecentPlay(Integer userId, Pageable pageable) {
        var pageResult = trackPlayRepository.findByUserId(userId, pageable);

        return PageResponse.from(
                pageResult.map(p -> trackMapper.toTrackResponse(p.getTrack())));
    }
}
