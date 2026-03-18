package com.popcorn.soundcloudclone.domain.repository;

import com.popcorn.soundcloudclone.domain.entity.PlaylistTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, Integer> {
    @Modifying // mark this query is update, delete or insert
    @Query("DELETE FROM PlaylistTrack at WHERE at.playlist.id = :playlistId")
    void deleteByPlaylistId(@Param("playlistId") int playlistId);

    int countByPlaylistId(int albumId);

    boolean existsByPlaylistIdAndTrackId(int albumId, int trackId);
}
