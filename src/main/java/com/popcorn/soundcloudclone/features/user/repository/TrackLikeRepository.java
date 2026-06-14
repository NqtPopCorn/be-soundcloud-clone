package com.popcorn.soundcloudclone.features.user.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.popcorn.soundcloudclone.features.user.entity.TrackLike;

import java.util.Optional;
import java.util.Set;

public interface TrackLikeRepository extends JpaRepository<TrackLike, Integer>, JpaSpecificationExecutor<TrackLike> {
    boolean existsByTrackIdAndUserId(int trackId, int userId);

    Optional<TrackLike> findByTrackIdAndUserId(int trackId, int userId);

    // Page<TrackLike> findByUserId(int userId, Pageable pageable);

    @Modifying
    @Query("UPDATE Track t SET t.likeCount = t.likeCount + 1 WHERE t.id = :id")
    void incrementLikeCount(@Param("id") int id);

    @Modifying
    @Query("UPDATE Track t SET t.likeCount = GREATEST(t.likeCount - 1, 0) WHERE t.id = :id")
    void decrementLikeCount(@Param("id") int id);

    @Query("SELECT f.track.id FROM TrackLike f WHERE f.user.id = :userId")
    Optional<Set<Integer>> getLikedTrackIds(@Param("userId") Integer userId);
}
