package com.popcorn.soundcloudclone.mapper;

import com.popcorn.soundcloudclone.domain.dto.album.AlbumResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumSummaryResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumUpdateRequest;
import com.popcorn.soundcloudclone.domain.entity.Album;
import com.popcorn.soundcloudclone.security.CurrentUserContext;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// Quan trọng: uses = { TrackMapper.class } để map được list tracks
@Mapper(componentModel = "spring", uses = { UserMapper.class, TrackMapper.class, SharedQualifier.class })
public abstract class AlbumMapper {

    @Autowired
    protected CurrentUserContext ctx;

    @Mapping(target = "user", source = "artist")
    @Mapping(target = "coverImageUrl", source = "coverImage.url")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "splitTags")
    @Mapping(target = "liked", expression = "java(ctx.isAlbumLiked(album.getId()))")
    @Mapping(target = "tracks", source = "joinTracks")
    public abstract AlbumResponse toAlbumResponse(Album album);

    @Mapping(target = "user", source = "artist")
    @Mapping(target = "coverImageUrl", source = "coverImage.url")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "splitTags")
    @Mapping(target = "liked", expression = "java(ctx.isAlbumLiked(album.getId()))")
    public abstract AlbumSummaryResponse toAlbumSummaryResponse(Album album);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(ignore = true, target = "coverImage")
    public abstract void updateAlbum(@MappingTarget Album album, AlbumUpdateRequest albumUpdateRequest);

    @Named("splitTags")
    protected List<String> splitTags(String tags) {
        if (tags == null)
            return List.of();
        return Arrays.stream(tags.split(",")).map(String::trim).collect(Collectors.toList());
    }
}