package com.popcorn.soundcloudclone.features.playlist.service.impl;

import com.popcorn.soundcloudclone.features.playlist.dto.request.PlaylistCreationRequest;
import com.popcorn.soundcloudclone.features.playlist.dto.request.PlaylistFilterRequest;
import com.popcorn.soundcloudclone.features.playlist.dto.request.PlaylistUpdateRequest;
import com.popcorn.soundcloudclone.features.playlist.dto.response.PlaylistResponse;
import com.popcorn.soundcloudclone.features.playlist.dto.response.PlaylistSummaryResponse;

import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.playlist.entity.Playlist;
import com.popcorn.soundcloudclone.features.playlist.entity.PlaylistTrack;
import com.popcorn.soundcloudclone.features.track.entity.Track;
import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.common.exception.ErrorCode;
import com.popcorn.soundcloudclone.features.playlist.mapper.PlaylistMapper;
import com.popcorn.soundcloudclone.features.playlist.repository.PlaylistRepository;
import com.popcorn.soundcloudclone.features.playlist.repository.PlaylistTrackRepository;
import com.popcorn.soundcloudclone.features.track.repository.TrackRepository;
import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;
import com.popcorn.soundcloudclone.features.playlist.repository.specification.PlaylistSpecification;
import com.popcorn.soundcloudclone.features.playlist.service.PlaylistService;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class PlaylistServiceImpl implements PlaylistService {
    PlaylistRepository playlistRepository;
    TrackRepository trackRepository;
    PlaylistMapper playlistMapper;
    PlaylistTrackRepository playlistTrackRepository;
    private final UserRepository userRepository;
    // private final FavoriteService favoriteService;

    @Override
    public PlaylistResponse getById(int id, int userId) {
        return playlistMapper.toPlaylistResponse(findPlaylistByIdOrThrow(id));
    }

    private Playlist findPlaylistByIdOrThrow(int id) {
        return playlistRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
    }

    private User findUserByIdOrThrow(int id) {
        return userRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
    }

    @Override
    public PageResponse<PlaylistResponse> findByFilter(PlaylistFilterRequest dto, Pageable pageable) {
        var spec = PlaylistSpecification.keywordContains(dto.getKeyword())
                .and(PlaylistSpecification.ownByArtistId(dto.getUserId()));
        var playlists = playlistRepository.findAll(spec, pageable);
        return PageResponse.from(playlists.map(playlistMapper::toPlaylistResponse));
    }

    @Override
    public PageResponse<PlaylistSummaryResponse> findSummaries(PlaylistFilterRequest dto, Pageable pageable) {
        var spec = PlaylistSpecification.keywordContains(dto.getKeyword())
                .and(PlaylistSpecification.ownByArtistId(dto.getUserId()));
        var playlists = playlistRepository.findAll(spec, pageable);
        return PageResponse.from(playlists.map(playlistMapper::toPlaylistSummaryResponse));
    }

    @Override
    public List<PlaylistSummaryResponse> getUserPlaylistSummaries(int userId) {
        return playlistRepository.findAllByCreatorId(userId).stream().map(playlistMapper::toPlaylistSummaryResponse)
                .toList();
    }

    @Override
    public PlaylistResponse create(int userId, PlaylistCreationRequest request) {
        var user = findUserByIdOrThrow(userId);
        // List<Track> playlistTracks = request.getTrackIds().stream().map(trackId ->
        // Track.builder().id(trackId).build()).toList();
        Playlist playlist = Playlist.builder()
                .name(request.getName())
                .creator(user)
                .isPublic(false)
                .build();
        return playlistMapper.toPlaylistResponse(playlistRepository.save(playlist));
    }

    @Override
    public void patchUpdatePlaylist(int id, PlaylistUpdateRequest request) {
        var playlist = playlistRepository.findById(id).orElseThrow(() -> new RuntimeException("Playlist not found"));
        playlistMapper.patchUpdate(playlist, request);
        playlistRepository.save(playlist);
    }

    @Override
    public void updatePlaylistTracks(int id, List<Integer> trackIds) {
        Playlist playlist = findPlaylistByIdOrThrow(id);

        // Xóa toàn bộ track cũ
        playlistTrackRepository.deleteByPlaylistId(id);

        // Chuẩn bị danh sách mới
        List<PlaylistTrack> playlistTracks = new ArrayList<>();
        for (int i = 0; i < trackIds.size(); i++) {
            int trackId = trackIds.get(i);
            var track = trackRepository.findById(trackId).orElseThrow(() -> new RuntimeException("Track not found"));
            PlaylistTrack playlistTrack = new PlaylistTrack();
            playlistTrack.setPlaylist(playlist);
            playlistTrack.setTrack(track);
            playlistTrack.setPosition(i + 1);
            playlistTracks.add(playlistTrack);
        }

        // Batch save
        playlistTrackRepository.saveAll(playlistTracks);
    }

    @Override
    @Transactional
    public void addTracksToPlaylist(int playlistId, List<Integer> trackIds) {
        var tracks = trackRepository.findByIdIn(trackIds);
        var playlist = findPlaylistByIdOrThrow(playlistId);
        int trackCount = playlistTrackRepository.countByPlaylistId(playlistId);
        try {
            for (Track track : tracks) {
                PlaylistTrack playlistTrack = PlaylistTrack.builder()
                        .playlist(playlist)
                        .track(track)
                        .position(trackCount)
                        .build();
                playlistTrackRepository.save(playlistTrack); // check unique
                trackCount++;
            }
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.DUPLICATED_CONSTRAINT);
        }
    }

    @Override
    public void deletePlaylist(int id) {
        var playlist = findPlaylistByIdOrThrow(id);

        playlistTrackRepository.deleteByPlaylistId(id);
        playlistTrackRepository.flush();
        playlistRepository.delete(playlist);
    }
}
