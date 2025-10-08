package com.popcorn.soundcloudclone.domain.mapper;

import com.popcorn.soundcloudclone.domain.dto.track.GenreResponse;
import com.popcorn.soundcloudclone.domain.entity.Genre;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreResponse toResponse(Genre genre);

    default List<GenreResponse> toListResponse(List<Genre> genres) {
        return genres.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
