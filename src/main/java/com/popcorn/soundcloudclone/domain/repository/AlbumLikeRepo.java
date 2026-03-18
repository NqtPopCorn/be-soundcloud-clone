package com.popcorn.soundcloudclone.domain.repository;

import com.popcorn.soundcloudclone.domain.entity.AlbumLike;
import com.popcorn.soundcloudclone.domain.entity.TrackLike;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.Optional;

public interface AlbumLikeRepo extends JpaRepository<AlbumLike, Integer> {

    boolean existsByAlbumIdAndUserId(int albumId, int userId);

    Optional<AlbumLike> findByAlbumIdAndUserId(int albumId, int userId);

    Page<AlbumLike> findByUserId(int userId, Pageable pageable);

    @Query("SELECT f.album.id FROM AlbumLike f WHERE f.user.id = :userId")
    Optional<Set<Integer>> getLikedAlbumIds(@Param("userId") Integer userId);
}
