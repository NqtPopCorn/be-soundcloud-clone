package com.popcorn.soundcloudclone.domain.mapper;

import com.popcorn.soundcloudclone.domain.dto.playlist.*;
import com.popcorn.soundcloudclone.domain.entity.Playlist;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class, TrackMapper.class})
//@RequiredArgsConstructor
public abstract class PlaylistMapper {

    @Mapping(target = "user", source = "creator")
    @Mapping(target = "tracks", source = "tracks")
    public abstract PlaylistResponse toPlaylistResponse(Playlist playlist);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "trackCount", expression = "java(playlist.getTracks().size())")
    @Mapping(target = "imageUrl", source = "tracks", qualifiedByName = "getFirstTrackImage")
    public abstract PlaylistSummaryResponse toPlaylistSummaryResponse(Playlist playlist);

    public List<PlaylistSummaryResponse> toListPlaylistSummary(List<Playlist> playlists) {
        return playlists.stream().map(this::toPlaylistSummaryResponse).collect(Collectors.toList());
    }
}
