package com.popcorn.soundcloudclone.domain.repository;

import com.popcorn.soundcloudclone.domain.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Integer>, JpaSpecificationExecutor<Track> {
    List<Track> findByIdIn(List<Integer> trackIds);

    Optional<Track> findByIdAndArtistId(Integer trackId, Integer artistId);

    @Modifying
    @Query("""
                UPDATE Track t SET t.playCount = t.playCount + 1 WHERE t.id = :trackId
            """)
    void increasePlayCount(@Param("trackId") int trackId);
}
