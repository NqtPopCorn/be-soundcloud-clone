package com.popcorn.soundcloudclone.domain.service.impl;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumSummaryResponse;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistResponse;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistSummaryResponse;
import com.popcorn.soundcloudclone.domain.dto.track.TrackResponse;
import com.popcorn.soundcloudclone.domain.entity.*;
import com.popcorn.soundcloudclone.domain.repository.*;
import com.popcorn.soundcloudclone.domain.service.FavoriteAlbumService;
import com.popcorn.soundcloudclone.domain.service.FavoritePlaylistService;
import com.popcorn.soundcloudclone.domain.service.FavoriteTrackService;
import com.popcorn.soundcloudclone.exception.ApplicationException;
import com.popcorn.soundcloudclone.exception.ErrorCode;
import com.popcorn.soundcloudclone.mapper.AlbumMapper;
import com.popcorn.soundcloudclone.mapper.PlaylistMapper;
import com.popcorn.soundcloudclone.mapper.TrackMapper;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

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

    @Override
    public Set<Integer> getLikedTrackIds(Integer userId) {
        if (userId == null)
            return new HashSet<>();
        return trackLikeRepository.getLikedTrackIds(userId).orElse(new HashSet<>());
    }

    @Override
    @Transactional
    public void likeTrack(int userId, int trackId) {
        if (!trackLikeRepository.existsByTrackIdAndUserId(trackId, userId)) {
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
    public void unlikeTrack(int userId, int trackId) {
        TrackLike trackLike = trackLikeRepository.findByTrackIdAndUserId(trackId, userId)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));
        trackLikeRepository.delete(trackLike);
        trackLikeRepository.flush();

        var track = findTrackByIdOrThrow(trackId);
        track.setLikeCount(track.getLikeCount() - 1);
    }

    @Override
    @Transactional
    public void likePlaylist(int userId, int playlistId) {
        // Lưu ý: Đã đổi thứ tự tham số trong hàm exists cho khớp với logic chung
        if (!playlistLikeRepo.existsByPlaylistIdAndUserId(playlistId, userId)) {
            // save to db
            Playlist playlist = findPlaylistByIdOrThrow(playlistId);
            User user = findUserOrThrow(userId);
            PlaylistLike playlistLike = PlaylistLike.builder()
                    .playlist(playlist)
                    .user(user)
                    .build();
            playlistLikeRepo.save(playlistLike);
            // increase like count for playlist
            playlistLikeRepo.flush();
            playlist.setLikeCount(playlist.getLikeCount() + 1);
        }
    }

    @Override
    @Transactional
    public void unlikePlaylist(int userId, int playlistId) {
        // Tìm record like
        PlaylistLike playlistLike = playlistLikeRepo.findByPlaylistIdAndUserId(playlistId, userId)
                .orElseThrow(() -> new ApplicationException("Record not found", ErrorCode.NOT_FOUND));

        // Xóa record like
        playlistLikeRepo.delete(playlistLike);
        playlistLikeRepo.flush();

        // Giảm count của playlist
        Playlist playlist = findPlaylistByIdOrThrow(playlistId);
        playlist.setLikeCount(playlist.getLikeCount() - 1);
    }

    @Override
    @Transactional
    public void likeAlbum(int userId, int albumId) {
        if (!albumLikeRepo.existsByAlbumIdAndUserId(albumId, userId)) {
            // save to db
            Album album = findAlbumByIdOrThrow(albumId);
            User user = findUserOrThrow(userId);
            AlbumLike albumLike = AlbumLike.builder()
                    .album(album)
                    .user(user)
                    .build();
            albumLikeRepo.save(albumLike);

            // increase like count for album
            albumLikeRepo.flush();
            album.setLikeCount(album.getLikeCount() + 1);
        }
    }

    @Override
    @Transactional
    public void unlikeAlbum(int userId, int albumId) {
        // Tìm record like
        AlbumLike albumLike = albumLikeRepo.findByAlbumIdAndUserId(albumId, userId)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));

        // Xóa record like
        albumLikeRepo.delete(albumLike);
        albumLikeRepo.flush();

        // Giảm count của album
        Album album = findAlbumByIdOrThrow(albumId);
        album.setLikeCount(album.getLikeCount() - 1);
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
    public Set<Integer> getLikedAlbumIds(Integer userId) {
        if (userId == null) {
            return new HashSet<>();
        }
        return albumLikeRepo.getLikedAlbumIds(userId).orElse(new HashSet<>());
    }

    @Override
    public Set<Integer> getLikedPlaylistIds(Integer userId) {
        if (userId == null) {
            return new HashSet<>();
        }
        return playlistLikeRepo.getLikedPlaylistIds(userId).orElse(new HashSet<>());
    }

    @Override
    public PageResponse<TrackResponse> getLikedTracks(Integer userId, Pageable pageable) {
        // var user = findUserOrThrow(userId);
        Specification<TrackLike> spec = (root, query, criteriaBuilder) -> criteriaBuilder
                .equal(root.get("user").get("id"), userId);
        return PageResponse.from(trackLikeRepository.findAll(spec, pageable)
                .map(trackLike -> trackMapper.toTrackResponse(trackLike.getTrack())));
    }

    @Override
    public PageResponse<AlbumSummaryResponse> getLikedAlbums(Integer userId, Pageable pageable) {
        // var user = findUserOrThrow(userId);
        return PageResponse.from(albumLikeRepo.findByUserId(userId, pageable).map(
                albumLike -> albumMapper.toAlbumSummaryResponse(albumLike.getAlbum())));
    }

    @Override
    public PageResponse<PlaylistSummaryResponse> getLikedPlaylists(Integer userId, Pageable pageable) {
        // var user = findUserOrThrow(userId);
        return PageResponse.from(playlistLikeRepo.findByUserId(userId, pageable).map(
                playlistLike -> playlistMapper.toPlaylistSummaryResponse(playlistLike.getPlaylist())));
    }
}