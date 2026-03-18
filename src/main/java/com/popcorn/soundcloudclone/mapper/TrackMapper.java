package com.popcorn.soundcloudclone.mapper;

import com.popcorn.soundcloudclone.domain.dto.track.*;
import com.popcorn.soundcloudclone.domain.entity.*;
import com.popcorn.soundcloudclone.security.CurrentUserContext;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", uses = { UserMapper.class, SharedQualifier.class })
public abstract class TrackMapper {

    @Autowired
    protected CurrentUserContext ctx;

    // ========================================================================
    // 1. BASE MAPPING (Common logic)
    // ========================================================================
    @Mapping(target = "genres", source = ".", qualifiedByName = "getGenres")
    @Mapping(target = "tags", source = ".", qualifiedByName = "splitTags")
    @Mapping(target = "audioUrl", source = "audioUpload.url")
    @Mapping(target = "imageUrl", source = "imageUpload.url")
    @Mapping(target = "isLiked", expression = "java(ctx.isTrackLiked(track.getId()))")
    protected abstract TrackResponse toTrackResponseBase(Track track);

    @InheritConfiguration(name = "toTrackResponseBase")
    public abstract TrackResponse toTrackResponse(Track track);

    @Mapping(target = ".", source = "track")
    @Mapping(target = "genres", source = "track", qualifiedByName = "getGenres")
    @Mapping(target = "tags", source = "track", qualifiedByName = "splitTags")
    @Mapping(target = "audioUrl", source = "track.audioUpload.url")
    @Mapping(target = "imageUrl", source = "track.imageUpload.url")
    @Mapping(target = "isLiked", expression = "java(ctx.isTrackLiked(albumTrack.getTrack().getId()))")
    public abstract TrackItemResponse toAlbumTrackResponse(AlbumTrack albumTrack);

    @Mapping(target = ".", source = "track")
    @Mapping(target = "genres", source = "track", qualifiedByName = "getGenres")
    @Mapping(target = "tags", source = "track", qualifiedByName = "splitTags")
    @Mapping(target = "audioUrl", source = "track.audioUpload.url")
    @Mapping(target = "imageUrl", source = "track.imageUpload.url")
    @Mapping(target = "isLiked", expression = "java(ctx.isTrackLiked(playlistTrack.getTrack().getId()))")
    public abstract TrackItemResponse toPlaylistTrackResponse(PlaylistTrack playlistTrack);

    // ========================================================================
    // 3. ENTITY UPDATE / CREATE
    // ========================================================================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "audioUpload", ignore = true)
    @Mapping(target = "imageUpload", ignore = true)
    public abstract Track toTrack(TrackCreationRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "audioUpload", ignore = true)
    @Mapping(target = "imageUpload", ignore = true)
    public abstract void updateTrack(@MappingTarget Track track, TrackUpdateRequest updateRequest);

    // ========================================================================
    // 4. HELPERS
    // ========================================================================
    @Named("getGenres")
    protected List<String> getGenres(Track track) {
        if (track.getGenres() == null)
            return List.of();
        return track.getGenres().stream().map(Genre::getName).collect(Collectors.toList());
    }

    @Named("splitTags")
    protected List<String> splitTags(Track track) {
        if (track.getTags() == null)
            return List.of();
        return Stream.of(track.getTags().split(",")).map(String::trim).collect(Collectors.toList());
    }
}