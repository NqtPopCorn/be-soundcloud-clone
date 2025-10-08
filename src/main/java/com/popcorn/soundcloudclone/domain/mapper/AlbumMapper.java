package com.popcorn.soundcloudclone.domain.mapper;

import com.popcorn.soundcloudclone.domain.dto.album.AlbumResponse;
import com.popcorn.soundcloudclone.domain.entity.Album;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class, TrackMapper.class, SharedQualifier.class})
public abstract class AlbumMapper {
//
//    @Value("${app.file-base-url}")
//    protected String fileBaseUrl;

    @Mapping(target = "user", source = "artist")
    @Mapping(target = "tracks", source = "joinTracks")
    @Mapping(target = "coverImageUrl", source = "coverUpload", qualifiedByName = "getImageUrl")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "splitTags")
    public abstract AlbumResponse toAlbumResponse(Album album, @Context List<Integer> likedTrackIds);

    @Named("splitTags")
    protected List<String> splitTags(String tags) {
        if(tags == null) return List.of();
        return Arrays.stream(tags.split(",")).map(String::trim).collect(Collectors.toList());
    }

}
