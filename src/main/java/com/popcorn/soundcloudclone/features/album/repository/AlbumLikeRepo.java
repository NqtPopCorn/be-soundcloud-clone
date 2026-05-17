package com.popcorn.soundcloudclone.features.album.repository;

import com.popcorn.soundcloudclone.features.album.entity.AlbumLike;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.Optional;

public interface AlbumLikeRepo extends JpaRepository<AlbumLike, Integer> {

    boolean existsByAlbumIdAndUserId(int albumId, int userId);

    Optional<AlbumLike> findByAlbumIdAndUserId(int albumId, int userId);

    Page<AlbumLike> findByUserId(int userId, Pageable pageable);

    @Modifying
    @Query("UPDATE Album a SET a.likeCount = a.likeCount + 1 WHERE a.id = :id")
    void incrementLikeCount(@Param("id") int id);

    @Modifying
    @Query("UPDATE Album a SET a.likeCount = GREATEST(a.likeCount - 1, 0) WHERE a.id = :id")
    void decrementLikeCount(@Param("id") int id);

    @Query("SELECT f.album.id FROM AlbumLike f WHERE f.user.id = :userId")
    Optional<Set<Integer>> getLikedAlbumIds(@Param("userId") Integer userId);
}
