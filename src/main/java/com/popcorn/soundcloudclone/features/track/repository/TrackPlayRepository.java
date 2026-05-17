package com.popcorn.soundcloudclone.features.track.repository;

import com.popcorn.soundcloudclone.features.track.entity.TrackPlay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TrackPlayRepository extends JpaRepository<TrackPlay, Integer>, JpaSpecificationExecutor<TrackPlay> {
    Page<TrackPlay> findByUserId(Integer userId, Pageable pageable);

    boolean existsByTrackIdAndUserId(int trackId, int userId);
}
