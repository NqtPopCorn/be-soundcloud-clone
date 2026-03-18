package com.popcorn.soundcloudclone.domain.repository;

import com.popcorn.soundcloudclone.domain.entity.UserFollower;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserFollowerRepository
        extends JpaRepository<UserFollower, Integer>, JpaSpecificationExecutor<UserFollower> {
    boolean existsByUserIdAndArtistId(int userId, int artistId);

    Optional<UserFollower> findByUserIdAndArtistId(int userId, int artistId);

    Page<UserFollower> findByUserId(int userId, Pageable pageable);

    void deleteByUserIdAndArtistId(int userId, int artistId);
}
