package com.popcorn.soundcloudclone.features.track.mapper;

import com.popcorn.soundcloudclone.features.album.entity.AlbumTrack;
import com.popcorn.soundcloudclone.features.genre.entity.Genre;
import com.popcorn.soundcloudclone.features.playlist.entity.PlaylistTrack;
import com.popcorn.soundcloudclone.features.track.dto.request.TrackCreationRequest;
import com.popcorn.soundcloudclone.features.track.dto.request.TrackUpdateRequest;
import com.popcorn.soundcloudclone.features.track.dto.response.TrackItemResponse;
import com.popcorn.soundcloudclone.features.track.dto.response.TrackResponse;
import com.popcorn.soundcloudclone.features.track.entity.Track;
import com.popcorn.soundcloudclone.features.users.mapper.UserMapper;
import com.popcorn.soundcloudclone.common.utils.SharedQualifier;
import com.popcorn.soundcloudclone.common.security.CurrentUserContext;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", uses = { UserMapper.class, SharedQualifier.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TrackMapper {

    // Removed CurrentUserContext for stateless mapping


    // ========================================================================
    // 1. BASE MAPPING (Common logic)
    // ========================================================================
    @Mapping(target = "genres", source = ".", qualifiedByName = "getGenres")
    @Mapping(target = "tags", source = ".", qualifiedByName = "splitTags")
    @Mapping(target = "audioUrl", source = "audioUrl")
    @Mapping(target = "imageUrl", source = "imageUrl")
    protected abstract TrackResponse toTrackResponseBase(Track track);

    public TrackResponse toTrackResponse(Track track) {
        return toTrackResponseBase(track);
    }

    // Removed inherited method with context

    @Mapping(target = ".", source = "track")
    @Mapping(target = "genres", source = "track", qualifiedByName = "getGenres")
    @Mapping(target = "tags", source = "track", qualifiedByName = "splitTags")
    @Mapping(target = "audioUrl", source = "track.audioUrl")
    @Mapping(target = "imageUrl", source = "track.imageUrl")
    public abstract TrackItemResponse toAlbumTrackResponse(AlbumTrack albumTrack);

    @Mapping(target = ".", source = "track")
    @Mapping(target = "genres", source = "track", qualifiedByName = "getGenres")
    @Mapping(target = "tags", source = "track", qualifiedByName = "splitTags")
    @Mapping(target = "audioUrl", source = "track.audioUrl")
    @Mapping(target = "imageUrl", source = "track.imageUrl")
    public abstract TrackItemResponse toPlaylistTrackResponse(PlaylistTrack playlistTrack);

    // ========================================================================
    // 3. ENTITY UPDATE / CREATE
    // ========================================================================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "genres", ignore = true)
    public abstract Track toTrack(TrackCreationRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "genres", ignore = true)
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
