package com.popcorn.soundcloudclone.features.playlist.mapper;

import com.popcorn.soundcloudclone.features.playlist.dto.request.PlaylistUpdateRequest;
import com.popcorn.soundcloudclone.features.playlist.dto.response.PlaylistResponse;
import com.popcorn.soundcloudclone.features.playlist.dto.response.PlaylistSummaryResponse;
import com.popcorn.soundcloudclone.features.playlist.entity.PlaylistTrack;
import com.popcorn.soundcloudclone.features.track.dto.response.TrackItemResponse;

import com.popcorn.soundcloudclone.features.track.mapper.TrackMapper;
import com.popcorn.soundcloudclone.features.users.mapper.UserMapper;
import com.popcorn.soundcloudclone.features.playlist.entity.Playlist;
import com.popcorn.soundcloudclone.common.security.CurrentUserContext;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

// Uses TrackMapper để map PlaylistTrack -> TrackItemResponse
@Mapper(componentModel = "spring", uses = { UserMapper.class, TrackMapper.class })
public abstract class PlaylistMapper {

    // Removed CurrentUserContext for stateless mapping


    @Mapping(target = "user", source = "creator")
    @Mapping(target = "tracks", source = "joinTracks")
    public abstract PlaylistResponse toPlaylistResponseBase(Playlist playlist);

    public PlaylistResponse toPlaylistResponse(Playlist playlist) {
        return toPlaylistResponseBase(playlist);
    }

    // Method này không cần check like track nên không cần Context
    @Mapping(target = "name", source = "name")
    @Mapping(target = "user", source = "creator")
    @Mapping(target = "trackCount", expression = "java(playlist.getJoinTracks() != null ? playlist.getJoinTracks().size() : 0)")
    public abstract PlaylistSummaryResponse toPlaylistSummaryResponseBase(Playlist playlist);

    public PlaylistSummaryResponse toPlaylistSummaryResponse(Playlist playlist) {
        return toPlaylistSummaryResponseBase(playlist);
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchUpdate(@MappingTarget Playlist playlist, PlaylistUpdateRequest request);

}
