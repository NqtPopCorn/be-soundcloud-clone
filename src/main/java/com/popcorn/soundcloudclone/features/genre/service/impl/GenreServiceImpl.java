package com.popcorn.soundcloudclone.features.genre.service.impl;

import com.popcorn.soundcloudclone.features.genre.dto.response.GenreResponse;
import com.popcorn.soundcloudclone.features.genre.entity.Genre;
import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.common.exception.ErrorCode;
import com.popcorn.soundcloudclone.features.genre.mapper.GenreMapper;
import com.popcorn.soundcloudclone.features.genre.repository.GenreRepository;
import com.popcorn.soundcloudclone.features.genre.repository.TrackGenreRepository;
import com.popcorn.soundcloudclone.features.genre.service.GenreService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class GenreServiceImpl implements GenreService {
    private GenreRepository genreRepository;
    private GenreMapper genreMapper;
    private TrackGenreRepository trackGenreRepository;

    @Override
    public GenreResponse findByName(String name) {
        return genreMapper.toResponse(genreRepository.findByName(name)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TAG_NOT_FOUND)));
    }

    @Override
    public GenreResponse create(String tagName) {
        return genreMapper.toResponse(genreRepository.save(
                Genre.builder().name(tagName).build()));
    }

    @Override
    @Transactional
    public int delete(int genreId) {
        Genre found = genreRepository.findById(genreId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TAG_NOT_FOUND));
        trackGenreRepository.deleteAllByGenreId(genreId);
        genreRepository.delete(found);
        return 1;
    }

    @Override
    public GenreResponse update(int id, String newName) {
        Genre found = genreRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TAG_NOT_FOUND));
        found.setName(newName);
        return genreMapper.toResponse(genreRepository.save(found));
    }

    @Override
    public List<GenreResponse> findAll() {
        return genreMapper.toListResponse(genreRepository.findAll());
    }

}
