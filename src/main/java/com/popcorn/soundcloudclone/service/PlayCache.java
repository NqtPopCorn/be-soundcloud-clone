package com.popcorn.soundcloudclone.service;

import java.time.Duration;

public interface PlayCache {
    boolean hasPlay(Integer userId, Integer trackId);

    void savePlay(Integer userId, Integer trackId, Duration ttl);
}