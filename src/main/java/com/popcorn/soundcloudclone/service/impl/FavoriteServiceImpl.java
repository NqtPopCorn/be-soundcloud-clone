package com.popcorn.soundcloudclone.service.impl;

import com.popcorn.soundcloudclone.repository.TrackLikeRepository;
import com.popcorn.soundcloudclone.service.FavoriteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final TrackLikeRepository trackLikeRepository;

    @Override
    public List<Integer> getLikedTrackIds(Integer userId) {
        if(userId == null) return new ArrayList<>();
        return trackLikeRepository.getLikedTrackIds(userId).orElse(new ArrayList<>());
    }

    @Override
    public void likeTrack(int userId, int trackId) {

    }

    @Override
    public void unlikeTrack(int userId, int trackId) {

    }

    @Override
    public void likePlaylist(int userId, int playlistId) {

    }

    @Override
    public void unlikePlaylist(int userId, int playlistId) {

    }

    @Override
    public void likeAlbum(int userId, int albumId) {

    }

    @Override
    public void unlikeAlbum(int userId, int albumId) {

    }
}
