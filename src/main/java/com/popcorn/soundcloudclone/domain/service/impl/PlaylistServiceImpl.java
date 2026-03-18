package com.popcorn.soundcloudclone.domain.service.impl;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.playlist.*;
import com.popcorn.soundcloudclone.domain.entity.Playlist;
import com.popcorn.soundcloudclone.domain.entity.PlaylistTrack;
import com.popcorn.soundcloudclone.domain.entity.Track;
import com.popcorn.soundcloudclone.domain.entity.User;
import com.popcorn.soundcloudclone.exception.ApplicationException;
import com.popcorn.soundcloudclone.exception.ErrorCode;
import com.popcorn.soundcloudclone.mapper.PlaylistMapper;
import com.popcorn.soundcloudclone.domain.repository.PlaylistRepository;
import com.popcorn.soundcloudclone.domain.repository.PlaylistTrackRepository;
import com.popcorn.soundcloudclone.domain.repository.TrackRepository;
import com.popcorn.soundcloudclone.domain.repository.UserRepository;
import com.popcorn.soundcloudclone.domain.repository.specification.PlaylistSpecification;
import com.popcorn.soundcloudclone.domain.service.PlaylistService;

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
