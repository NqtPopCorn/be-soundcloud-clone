package com.popcorn.soundcloudclone.features.album.mapper;

import com.popcorn.soundcloudclone.features.track.mapper.TrackMapper;
import com.popcorn.soundcloudclone.features.users.mapper.UserMapper;
import com.popcorn.soundcloudclone.common.utils.SharedQualifier;
import com.popcorn.soundcloudclone.features.album.dto.response.AlbumResponse;
import com.popcorn.soundcloudclone.features.album.dto.response.AlbumSummaryResponse;
import com.popcorn.soundcloudclone.features.album.dto.request.AlbumUpdateRequest;
import com.popcorn.soundcloudclone.features.album.entity.Album;
import com.popcorn.soundcloudclone.common.security.CurrentUserContext;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { UserMapper.class, TrackMapper.class, SharedQualifier.class })
public abstract class AlbumMapper {

    // Removed CurrentUserContext for stateless mapping


    @Mapping(target = "user", source = "artist")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "splitTags")
    @Mapping(target = "tracks", source = "joinTracks")
    public abstract AlbumResponse toAlbumResponseBase(Album album);

    public AlbumResponse toAlbumResponse(Album album) {
        return toAlbumResponseBase(album);
    }

    @Mapping(target = "user", source = "artist")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "splitTags")
    public abstract AlbumSummaryResponse toAlbumSummaryResponseBase(Album album);

    public AlbumSummaryResponse toAlbumSummaryResponse(Album album) {
        return toAlbumSummaryResponseBase(album);
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateAlbum(@MappingTarget Album album, AlbumUpdateRequest albumUpdateRequest);

    @Named("splitTags")
    protected List<String> splitTags(String tags) {
        if (tags == null)
            return List.of();
        return Arrays.stream(tags.split(",")).map(String::trim).collect(Collectors.toList());
    }
}
