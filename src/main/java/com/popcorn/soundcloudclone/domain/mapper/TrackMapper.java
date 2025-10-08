package com.popcorn.soundcloudclone.domain.mapper;

import com.popcorn.soundcloudclone.domain.dto.track.*;
import com.popcorn.soundcloudclone.domain.entity.AlbumTrack;
import com.popcorn.soundcloudclone.domain.entity.Genre;
import com.popcorn.soundcloudclone.domain.entity.Track;
import com.popcorn.soundcloudclone.dto.track.*;
import com.popcorn.soundcloudclone.entity.*;
import lombok.RequiredArgsConstructor;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", uses = {UserMapper.class, SharedQualifier.class})
@RequiredArgsConstructor
public abstract class TrackMapper {

    // TO DTO
    // method gốc chứa config chung
    @Mapping(target = "genres", source = ".", qualifiedByName = "getGenres")
    @Mapping(target = "tags", source = ".", qualifiedByName = "splitTags")
    @Mapping(target = "audioUrl", source = ".", qualifiedByName = "getAudioUrl")
    @Mapping(target = "imageUrl", source = "imageUpload", qualifiedByName = "getImageUrl")
    protected abstract TrackResponse toTrackResponseBase(Track track);

    @InheritConfiguration(name = "toTrackResponseBase")
    @Mapping(target = "isLiked", expression = "java(likedTrackIds.contains(track.getId()))")
    public abstract TrackResponse toTrackResponse(Track track, @Context List<Integer> likedTrackIds);

    @InheritConfiguration(name = "toTrackResponseBase")
    public abstract TrackResponse toTrackResponse(Track track);

    @InheritConfiguration(name = "toTrackResponseBase")
    @Mapping(target = "privacy", expression = "java(track.getPrivacy().toString())")
    public abstract ArtistTrackResponse toArtistTrackResponse(Track track);

    @Mapping(target = "genres", source = "track", qualifiedByName = "getGenres")
    @Mapping(target = "tags", source = "track", qualifiedByName = "splitTags")
    @Mapping(target = "audioUrl", source = "track", qualifiedByName = "getAudioUrl")
    @Mapping(target = "imageUrl", source = "track.imageUpload", qualifiedByName = "getImageUrl")
    @Mapping(target = "isLiked", expression = "java(likedTrackIds.contains(albumTrack.getTrack().getId()))")
    @Mapping(target = ".", source = "track")
    public abstract TrackItemResponse toTrackItemResponse(AlbumTrack albumTrack, @Context List<Integer> likedTrackIds);

    // TO ENTITY
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "privacy", source = "privacy", qualifiedByName = "getPrivacy")
    @Mapping(target = "genres", ignore = true) // map o service
    @Mapping(target = "audioUpload", ignore = true)
    @Mapping(target = "imageUpload", ignore = true)
    public abstract Track toTrack(TrackCreationRequest request);

    // UPDATE ENTITY
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // ignore null field
//    @Mapping(target = "privacy", source = "privacy", qualifiedByName = "getPrivacy")
//    @Mapping(target = "genres", ignore = true)
//    @Mapping(target = "audioUpload", ignore = true)
//    @Mapping(target = "imageUpload", ignore = true)
    @InheritConfiguration(name = "toTrack")
    public abstract void updateTrack(@MappingTarget Track track, TrackUpdateRequest updateRequest);

    @Named("getPrivacy")
    protected Track.Privacy getPrivacy(String privacy) {
        return Track.Privacy.valueOf(privacy);
    }

    @Named("getArtistName")
    protected String getArtistName(Track track) {
        return track.getArtist().getStageName();
    }

    @Named("getGenres")
    protected List<String> getGenres(Track track) {
        if(track.getGenres() == null) return null;
        return track.getGenres().stream().map(Genre::getName).collect(Collectors.toList());
    }

//    protected List<String> toListGenreString(List<Genre> genres) {
//        if(genres == null) return null;
//        return genres.stream().map(Genre::getName).collect(Collectors.toList());
//    }
//
//    protected List<String> toListTagString(String tags) {
//        if(tags == null) return null;
//        return Stream.of(tags.split(",")).map(String::trim).collect(Collectors.toList());
//    }

    @Named("splitTags")
    protected List<String> splitTags(Track track) {
        if(track.getTags() == null) return null;
        return Stream.of(track.getTags().split(",")).map(String::trim).collect(Collectors.toList());
    }

    @Named("getOwnerId")
    protected Integer getOwnerId(Track track) {
        return track.getId();
    }

}
