package com.popcorn.soundcloudclone.features.genre.service;

import com.popcorn.soundcloudclone.features.genre.dto.response.GenreResponse;

import java.util.List;

public interface GenreService {
    GenreResponse findByName(String name);

    GenreResponse create(String tagName);

    int delete(int genreId);

    GenreResponse update(int id, String newName);

    List<GenreResponse> findAll();
}
