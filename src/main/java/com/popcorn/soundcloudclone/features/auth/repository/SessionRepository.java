package com.popcorn.soundcloudclone.features.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.popcorn.soundcloudclone.features.auth.entity.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
    Optional<Session> findByRefreshTokenHash(String refreshTokenHash);

    Optional<Session> findByUserId(int userId);
}
