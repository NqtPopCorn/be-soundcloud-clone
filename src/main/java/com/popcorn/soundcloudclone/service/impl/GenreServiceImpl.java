package com.popcorn.soundcloudclone.service.impl;

import com.popcorn.soundcloudclone.domain.dto.track.GenreResponse;
import com.popcorn.soundcloudclone.domain.entity.Genre;
import com.popcorn.soundcloudclone.exception.BadRequestException;
import com.popcorn.soundcloudclone.exception.ErrorCode;
import com.popcorn.soundcloudclone.domain.mapper.GenreMapper;
import com.popcorn.soundcloudclone.repository.GenreRepository;
import com.popcorn.soundcloudclone.service.GenreService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GenreServiceImpl implements GenreService {
    private GenreRepository genreRepository;
    private GenreMapper genreMapper;

    @Override
    public GenreResponse findByName(String name) {
        return genreMapper.toResponse(genreRepository.findByName(name)
                .orElseThrow(() -> new BadRequestException(ErrorCode.TAG_NOT_FOUND)));
    }

    @Override
    public GenreResponse create(String tagName) {
        return genreMapper.toResponse(genreRepository.save(
                Genre.builder().name(tagName).build()));
    }

    @Override
    public int delete(String tagName) {
        Genre found = genreRepository.findByName(tagName).orElseThrow(() -> new BadRequestException(ErrorCode.TAG_NOT_FOUND));
        genreRepository.delete(found);
        return 1;
    }

    @Override
    public List<GenreResponse> findAll() {
        return genreMapper.toListResponse(genreRepository.findAll());
    }


}
