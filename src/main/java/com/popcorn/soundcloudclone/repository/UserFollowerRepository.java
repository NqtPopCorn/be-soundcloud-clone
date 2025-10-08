package com.popcorn.soundcloudclone.repository;

import com.popcorn.soundcloudclone.domain.entity.UserFollower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserFollowerRepository extends JpaRepository<UserFollower, Integer> {
    boolean existsByUserIdAndArtistId(int userId, int artistId);
    Optional<UserFollower> findByUserIdAndArtistId(int userId, int artistId);
}
