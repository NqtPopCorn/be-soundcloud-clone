package com.popcorn.soundcloudclone.domain.repository;

import com.popcorn.soundcloudclone.domain.entity.TrackLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TrackLikeRepository extends JpaRepository<TrackLike, Integer>, JpaSpecificationExecutor<TrackLike> {
    boolean existsByTrackIdAndUserId(int trackId, int userId);

    Optional<TrackLike> findByTrackIdAndUserId(int trackId, int userId);

    // Page<TrackLike> findByUserId(int userId, Pageable pageable);

    @Query("SELECT f.track.id FROM TrackLike f WHERE f.user.id = :userId")
    Optional<Set<Integer>> getLikedTrackIds(@Param("userId") Integer userId);
}
