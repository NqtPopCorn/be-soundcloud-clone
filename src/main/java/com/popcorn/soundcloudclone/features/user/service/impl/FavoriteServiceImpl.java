package com.popcorn.soundcloudclone.features.user.service.impl;

import com.popcorn.soundcloudclone.features.album.entity.Album;
import com.popcorn.soundcloudclone.features.album.repository.AlbumRepository;
import com.popcorn.soundcloudclone.features.playlist.entity.Playlist;
import com.popcorn.soundcloudclone.features.playlist.repository.PlaylistRepository;
import com.popcorn.soundcloudclone.features.track.entity.Track;
import com.popcorn.soundcloudclone.features.track.repository.TrackRepository;
import com.popcorn.soundcloudclone.features.user.entity.AlbumLike;
import com.popcorn.soundcloudclone.features.user.entity.PlaylistLike;
import com.popcorn.soundcloudclone.features.user.entity.TrackLike;
import com.popcorn.soundcloudclone.features.user.repository.AlbumLikeRepo;
import com.popcorn.soundcloudclone.features.user.repository.PlaylistLikeRepo;
import com.popcorn.soundcloudclone.features.user.repository.TrackLikeRepository;
import com.popcorn.soundcloudclone.features.user.service.FavoriteAlbumService;
import com.popcorn.soundcloudclone.features.user.service.FavoritePlaylistService;
import com.popcorn.soundcloudclone.features.user.service.FavoriteTrackService;
import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;
import com.popcorn.soundcloudclone.common.security.CurrentUserContext;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.album.dto.response.AlbumResponse;
import com.popcorn.soundcloudclone.features.album.dto.response.AlbumSummaryResponse;
import com.popcorn.soundcloudclone.features.playlist.dto.response.PlaylistResponse;
import com.popcorn.soundcloudclone.features.playlist.dto.response.PlaylistSummaryResponse;
import com.popcorn.soundcloudclone.features.track.dto.response.TrackResponse;
import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.common.exception.ErrorCode;
import com.popcorn.soundcloudclone.features.album.mapper.AlbumMapper;
import com.popcorn.soundcloudclone.features.playlist.mapper.PlaylistMapper;
import com.popcorn.soundcloudclone.features.track.mapper.TrackMapper;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class FavoriteServiceImpl implements FavoritePlaylistService, FavoriteTrackService, FavoriteAlbumService {
    private final TrackLikeRepository trackLikeRepository;
    private final AlbumLikeRepo albumLikeRepo;
    private final PlaylistLikeRepo playlistLikeRepo;
    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final TrackMapper trackMapper;
    private final AlbumMapper albumMapper;
    private final PlaylistMapper playlistMapper;
    private final CurrentUserContext currentUserContext;

    @Override
    // @Cacheable(value = "likedTrackIds", key = "#userId", unless = "#result == null || #result.isEmpty()")
    public Set<Integer> getLikedTrackIds(Integer userId) {
        if (userId == null)
            return new HashSet<>();

        return trackLikeRepository
                .getLikedTrackIds(userId)
                .orElse(new HashSet<>());
    }

    @Override
    @Cacheable(value = "likedAlbumIds", key = "#userId", unless = "#result == null || #result.isEmpty()")
    public Set<Integer> getLikedAlbumIds(Integer userId) {

        if (userId == null) {
            return new HashSet<>();
        }

        return albumLikeRepo
                .getLikedAlbumIds(userId)
                .orElse(new HashSet<>());
    }

    @Override
    @Cacheable(value = "likedPlaylistIds", key = "#userId", unless = "#result == null || #result.isEmpty()")
    public Set<Integer> getLikedPlaylistIds(Integer userId) {

        if (userId == null) {
            return new HashSet<>();
        }

        return playlistLikeRepo
                .getLikedPlaylistIds(userId)
                .orElse(new HashSet<>());
    }

    @Override
    @Transactional
    @CacheEvict(value = "likedTrackIds", key = "#userId")
    public void toggleLikeTrack(int userId, int trackId, boolean liked) {
        if (liked) {
            Track track = findTrackByIdOrThrow(trackId);
            User user = findUserOrThrow(userId);
            TrackLike trackLike = TrackLike.builder()
                    .track(track)
                    .user(user)
                    .build();
            trackLikeRepository.save(trackLike);
            trackLikeRepository.incrementLikeCount(trackId);
        } else {
            var existingLike = trackLikeRepository.findByTrackIdAndUserId(trackId, userId);
            existingLike.orElseThrow(() -> new ApplicationException("Not liked yet", ErrorCode.NOT_FOUND));
            if (existingLike.isPresent()) {
                trackLikeRepository.delete(existingLike.get());
                trackLikeRepository.flush();
                trackLikeRepository.decrementLikeCount(trackId);
            }
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "likedPlaylistIds", key = "#userId")
    public void toggleLikePlaylist(int userId, int playlistId, boolean liked) {

        if (liked) {
            Playlist playlist = findPlaylistByIdOrThrow(playlistId);
            User user = findUserOrThrow(userId);
            PlaylistLike playlistLike = PlaylistLike.builder()
                    .playlist(playlist)
                    .user(user)
                    .build();
            playlistLikeRepo.save(playlistLike);
            playlistLikeRepo.incrementLikeCount(playlistId);
        } else {
            var existingLike = playlistLikeRepo.findByPlaylistIdAndUserId(playlistId, userId);
            existingLike.orElseThrow(() -> new ApplicationException("Not liked yet", ErrorCode.NOT_FOUND));
            if (existingLike.isPresent()) {
                playlistLikeRepo.delete(existingLike.get());
                playlistLikeRepo.flush();
                playlistLikeRepo.decrementLikeCount(playlistId);
            }
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "likedAlbumIds", key = "#userId")
    public void toggleLikeAlbum(int userId, int albumId, boolean liked) {

        if (liked) {
            Album album = findAlbumByIdOrThrow(albumId);
            User user = findUserOrThrow(userId);
            var existingLike = albumLikeRepo.findByAlbumIdAndUserId(albumId, userId);
            if (existingLike.isPresent()) {
                throw new ApplicationException("Already liked", ErrorCode.BAD_REQUEST);
            }
            AlbumLike albumLike = AlbumLike.builder()
                    .album(album)
                    .user(user)
                    .build();
            albumLikeRepo.save(albumLike);
            albumLikeRepo.incrementLikeCount(albumId);
        }

        else {
            var existingLike = albumLikeRepo.findByAlbumIdAndUserId(albumId, userId);
            existingLike.orElseThrow(() -> new ApplicationException("Not liked yet", ErrorCode.NOT_FOUND));
            if (existingLike.isPresent()) {
                albumLikeRepo.delete(existingLike.get());
                albumLikeRepo.flush();
                albumLikeRepo.decrementLikeCount(albumId);
            }
        }
    }

    // --- Helper Methods ---

    private Track findTrackByIdOrThrow(int trackId) {
        return trackRepository.findById(trackId).orElseThrow(() -> new ApplicationException(ErrorCode.TRACK_NOT_FOUND));
    }

    private Album findAlbumByIdOrThrow(int albumId) {
        return albumRepository.findById(albumId).orElseThrow(() -> new ApplicationException(ErrorCode.ALBUM_NOT_FOUND));
    }

    private Playlist findPlaylistByIdOrThrow(int playlistId) {
        return playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.PLAYLIST_NOT_FOUND));
    }

    private User findUserOrThrow(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public PageResponse<TrackResponse> getLikedTracks(Integer userId, Pageable pageable) {
        Specification<TrackLike> spec = (root, query, criteriaBuilder) -> criteriaBuilder
                .equal(root.get("user").get("id"), userId);
        return PageResponse.from(trackLikeRepository.findAll(spec, pageable)
                .map(trackLike -> trackMapper.toTrackResponse(trackLike.getTrack())));
    }

    @Override
    public PageResponse<AlbumSummaryResponse> getLikedAlbums(Integer userId, Pageable pageable) {
        return PageResponse.from(albumLikeRepo.findByUserId(userId, pageable).map(
                albumLike -> albumMapper.toAlbumSummaryResponse(albumLike.getAlbum())));
    }

    @Override
    public PageResponse<PlaylistSummaryResponse> getLikedPlaylists(Integer userId, Pageable pageable) {
        return PageResponse.from(playlistLikeRepo.findByUserId(userId, pageable).map(
                playlistLike -> playlistMapper.toPlaylistSummaryResponse(playlistLike.getPlaylist())));
    }
}
