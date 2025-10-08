package com.popcorn.soundcloudclone.service;
import java.util.List;

public interface FavoriteService {
    List<Integer> getLikedTrackIds(Integer userId);
}
