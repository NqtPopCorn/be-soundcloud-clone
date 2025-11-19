package com.popcorn.soundcloudclone.service;
import java.util.List;

// khong dung audit vi audit chi nen dung o low level logic con day la business logic
public interface FavoriteService {
    List<Integer> getLikedTrackIds(Integer userId);
    void likeTrack(int userId, int trackId);
    void unlikeTrack(int userId, int trackId);
    void likePlaylist(int userId, int playlistId);
    void unlikePlaylist(int userId, int playlistId);
    void likeAlbum(int userId, int albumId);
    void unlikeAlbum(int userId, int albumId);
}
