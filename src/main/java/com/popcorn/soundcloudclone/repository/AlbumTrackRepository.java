package com.popcorn.soundcloudclone.repository;

import com.popcorn.soundcloudclone.domain.entity.AlbumTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlbumTrackRepository extends JpaRepository<AlbumTrack, Integer> {
    @Modifying // mark this query is update, delete or insert
    @Query("DELETE FROM AlbumTrack at WHERE at.album.id = :albumId")
    void deleteByAlbumId(@Param("albumId") int albumId);

    int countByAlbumId(int albumId);

    boolean existsByAlbumIdAndTrackId(int albumId, int trackId);
}
