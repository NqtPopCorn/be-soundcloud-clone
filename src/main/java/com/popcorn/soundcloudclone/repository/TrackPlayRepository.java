package com.popcorn.soundcloudclone.repository;

import com.popcorn.soundcloudclone.domain.entity.TrackPlay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackPlayRepository extends JpaRepository<TrackPlay, Integer> {

    boolean existsByTrackIdAndUserId(int trackId, int userId);
}
