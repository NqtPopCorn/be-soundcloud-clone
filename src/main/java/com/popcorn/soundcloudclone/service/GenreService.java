package com.popcorn.soundcloudclone.service;

import com.popcorn.soundcloudclone.domain.dto.track.GenreResponse;

import java.util.List;

public interface GenreService {
    GenreResponse findByName(String name);
    GenreResponse create(String tagName);
    int delete(String tagName);
    List<GenreResponse> findAll();
}
