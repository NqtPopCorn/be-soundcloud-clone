package com.popcorn.soundcloudclone.features.playlist.repository;

import com.popcorn.soundcloudclone.features.playlist.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Integer>, JpaSpecificationExecutor<Playlist> {
    Optional<Playlist> findById(int id);

    List<Playlist> findAllByCreatorId(int userId);
}
