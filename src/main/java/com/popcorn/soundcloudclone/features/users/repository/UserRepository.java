package com.popcorn.soundcloudclone.features.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.popcorn.soundcloudclone.features.users.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.followersCount = u.followersCount + 1 WHERE u.id = :id")
    void incrementFollowersCount(@Param("id") int id);

    @Modifying
    @Query("UPDATE User u SET u.followersCount = GREATEST(u.followersCount - 1, 0) WHERE u.id = :id")
    void decrementFollowersCount(@Param("id") int id);

    @Modifying
    @Query("UPDATE User u SET u.followingCount = u.followingCount + 1 WHERE u.id = :id")
    void incrementFollowingCount(@Param("id") int id);

    @Modifying
    @Query("UPDATE User u SET u.followingCount = GREATEST(u.followingCount - 1, 0) WHERE u.id = :id")
    void decrementFollowingCount(@Param("id") int id);

}
