package com.popcorn.soundcloudclone.features.album.repository;

import com.popcorn.soundcloudclone.features.album.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Integer>, JpaSpecificationExecutor<Album> {
    Optional<Album> findById(int id);
}
