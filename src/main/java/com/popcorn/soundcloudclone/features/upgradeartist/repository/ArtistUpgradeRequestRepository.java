package com.popcorn.soundcloudclone.features.upgradeartist.repository;

import com.popcorn.soundcloudclone.features.upgradeartist.entity.ArtistUpgradeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistUpgradeRequestRepository extends JpaRepository<ArtistUpgradeRequest, Integer> {
    boolean existsByUserIdAndStatus(Integer userId, ArtistUpgradeRequest.Status status);

    Optional<ArtistUpgradeRequest> findFirstByUserIdOrderByCreatedAtDesc(Integer userId);

    Page<ArtistUpgradeRequest> findByStatus(ArtistUpgradeRequest.Status status, Pageable pageable);
}
