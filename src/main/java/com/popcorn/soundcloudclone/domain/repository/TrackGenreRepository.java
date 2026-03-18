package com.popcorn.soundcloudclone.domain.repository;

import com.popcorn.soundcloudclone.domain.entity.TrackGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackGenreRepository extends JpaRepository<TrackGenre, Integer> {
    void deleteAllByGenreId(int genreId);
}
