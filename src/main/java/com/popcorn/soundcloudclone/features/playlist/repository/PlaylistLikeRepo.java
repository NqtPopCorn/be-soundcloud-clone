package com.popcorn.soundcloudclone.features.playlist.repository;

import com.popcorn.soundcloudclone.features.playlist.entity.PlaylistLike;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.Set;

public interface PlaylistLikeRepo extends JpaRepository<PlaylistLike, Integer> {
    boolean existsByPlaylistIdAndUserId(int playlistId, int userId);

    Optional<PlaylistLike> findByPlaylistIdAndUserId(int playlistId, int userId);

    Page<PlaylistLike> findByUserId(int userId, Pageable pageable);

    @Modifying
    @Query("UPDATE Playlist p SET p.likeCount = p.likeCount + 1 WHERE p.id = :id")
    void incrementLikeCount(@Param("id") int id);

    @Modifying
    @Query("UPDATE Playlist p SET p.likeCount = GREATEST(p.likeCount - 1, 0) WHERE p.id = :id")
    void decrementLikeCount(@Param("id") int id);

    @Query("SELECT f.playlist.id FROM PlaylistLike f WHERE f.user.id = :userId")
    Optional<Set<Integer>> getLikedPlaylistIds(@Param("userId") Integer userId);
}
