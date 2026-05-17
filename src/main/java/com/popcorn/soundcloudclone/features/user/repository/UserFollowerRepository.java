package com.popcorn.soundcloudclone.features.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.popcorn.soundcloudclone.features.user.entity.UserFollower;

import java.util.Optional;

public interface UserFollowerRepository
        extends JpaRepository<UserFollower, Integer>, JpaSpecificationExecutor<UserFollower> {
    boolean existsByUserIdAndArtistId(int userId, int artistId);

    Optional<UserFollower> findByUserIdAndArtistId(int userId, int artistId);

    Page<UserFollower> findByUserId(int userId, Pageable pageable);

    Page<UserFollower> findByArtistId(int artistId, Pageable pageable);

    void deleteByUserIdAndArtistId(int userId, int artistId);
}
