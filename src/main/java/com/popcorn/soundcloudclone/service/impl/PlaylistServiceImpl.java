package com.popcorn.soundcloudclone.service.impl;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistCreationRequest;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistResponse;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistUpdateRequest;
import com.popcorn.soundcloudclone.domain.entity.Playlist;
import com.popcorn.soundcloudclone.domain.entity.PlaylistTrack;
import com.popcorn.soundcloudclone.domain.entity.Track;
import com.popcorn.soundcloudclone.domain.entity.User;
import com.popcorn.soundcloudclone.exception.BadRequestException;
import com.popcorn.soundcloudclone.exception.ErrorCode;
import com.popcorn.soundcloudclone.domain.mapper.PageResponseBuilder;
import com.popcorn.soundcloudclone.domain.mapper.PlaylistMapper;
import com.popcorn.soundcloudclone.repository.PlaylistRepository;
import com.popcorn.soundcloudclone.repository.PlaylistTrackRepository;
import com.popcorn.soundcloudclone.repository.TrackRepository;
import com.popcorn.soundcloudclone.repository.UserRepository;
import com.popcorn.soundcloudclone.repository.specification.PlaylistSpecification;
import com.popcorn.soundcloudclone.service.PlaylistService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    PageResponseBuilder<PlaylistResponse> pageResponseBuilder;
    PlaylistTrackRepository playlistTrackRepository;
    private final UserRepository userRepository;


    @Override
    public PlaylistResponse getById(int id) {
        return playlistMapper.toPlaylistResponse(findPlaylistByIdOrThrow(id));
    }
    
    private Playlist findPlaylistByIdOrThrow(int id) {
        return playlistRepository.findById(id).orElseThrow(()->new BadRequestException(ErrorCode.NOT_FOUND));
    }

    private User findUserByIdOrThrow(int id) {
        return userRepository.findById(id).orElseThrow(()->new BadRequestException(ErrorCode.NOT_FOUND));
    }

    @Override
    public PageResponse<PlaylistResponse> findByKeyword(String keyword, int page, int size, boolean asc) {
          Pageable pageable = PageRequest.of(page, size, asc? Sort.Direction.ASC : Sort.Direction.DESC, "id");
          var spec = PlaylistSpecification.keywordContains(keyword);
          var playlists = playlistRepository.findAll(spec, pageable);
          return pageResponseBuilder.toPageResponse(playlists.map(playlistMapper::toPlaylistResponse));
    }

    @Override
    public PlaylistResponse create(int userId, PlaylistCreationRequest request) {
        var user = findUserByIdOrThrow(userId);
//        List<Track> playlistTracks = request.getTrackIds().stream().map(trackId -> Track.builder().id(trackId).build()).toList();
        Playlist playlist = Playlist.builder()
                .name(request.getName())
                .creator(user)
                .isPublic(false)
                .build();
        return playlistMapper.toPlaylistResponse(playlistRepository.save(playlist));
    }

    @Override
    public void updatePlaylist(int id, PlaylistUpdateRequest request) {
        var playlist = playlistRepository.findById(id).orElseThrow(()->new RuntimeException("Playlist not found"));
        playlist.setName(request.getName());
        playlist.setPublic(request.getIsPublic());
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
            var track = trackRepository.findById(trackId).orElseThrow(()->new RuntimeException("Track not found"));
            PlaylistTrack playlistTrack = new PlaylistTrack();
            playlistTrack.setPlaylist(playlist);
            playlistTrack.setTrack(track);
            playlistTrack.setPosition(i+1);
            playlistTracks.add(playlistTrack);
        }

        // Batch save
        playlistTrackRepository.saveAll(playlistTracks);
    }

    @Override
    public void addTracksToPlaylist(int playlistId, List<Integer> trackIds) {
        var tracks = trackRepository.findByIdIn(trackIds);
        var playlist = findPlaylistByIdOrThrow(playlistId);
        for (Track track : tracks) {
            PlaylistTrack playlistTrack = PlaylistTrack.builder()
                    .playlist(playlist)
                    .track(track)
                    .position(playlistTrackRepository.countByPlaylistId(playlistId))
                    .build();
            playlistTrackRepository.save(playlistTrack);
        }

        playlistTrackRepository.flush();
    }

    @Override
    public void deletePlaylist(int id) {
        var playlist = findPlaylistByIdOrThrow(id);

        playlistTrackRepository.deleteByPlaylistId(id);
        playlistTrackRepository.flush();
        playlistRepository.delete(playlist);
    }
}
