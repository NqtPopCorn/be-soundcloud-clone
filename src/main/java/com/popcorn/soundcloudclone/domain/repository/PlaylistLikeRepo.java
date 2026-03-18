package com.popcorn.soundcloudclone.domain.repository;

import com.popcorn.soundcloudclone.domain.entity.PlaylistLike;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PlaylistLikeRepo extends JpaRepository<PlaylistLike, Integer> {
    boolean existsByPlaylistIdAndUserId(int playlistId, int userId);

    Optional<PlaylistLike> findByPlaylistIdAndUserId(int playlistId, int userId);

    Page<PlaylistLike> findByUserId(int userId, Pageable pageable);

    @Query("SELECT f.playlist.id FROM PlaylistLike f WHERE f.user.id = :userId")
    Optional<Set<Integer>> getLikedPlaylistIds(@Param("userId") Integer userId);
}
