package com.popcorn.soundcloudclone.repository;

import com.popcorn.soundcloudclone.domain.entity.TrackLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrackLikeRepository extends JpaRepository<TrackLike, Integer> {
    boolean existsByTrackIdAndUserId(int trackId, int userId);
    Optional<TrackLike> findByTrackIdAndUserId(int trackId, int userId);

    @Query("SELECT f.track.id FROM TrackLike f WHERE f.user.id = :userId")
    Optional<List<Integer>> getLikedTrackIds(@Param("userId") Integer userId);
}
