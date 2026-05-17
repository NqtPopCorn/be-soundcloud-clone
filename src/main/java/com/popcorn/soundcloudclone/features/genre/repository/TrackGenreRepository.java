package com.popcorn.soundcloudclone.features.genre.repository;

import com.popcorn.soundcloudclone.features.genre.entity.TrackGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackGenreRepository extends JpaRepository<TrackGenre, Integer> {
    void deleteAllByGenreId(int genreId);
}
