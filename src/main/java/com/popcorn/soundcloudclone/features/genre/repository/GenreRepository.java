package com.popcorn.soundcloudclone.features.genre.repository;

import com.popcorn.soundcloudclone.features.genre.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
    Optional<Genre> findByName(String name);

    List<Genre> findByIdIn(List<Integer> ids);
}
