package com.popcorn.soundcloudclone.mapper;

import com.popcorn.soundcloudclone.domain.dto.playlist.*;
import com.popcorn.soundcloudclone.domain.entity.Playlist;
import com.popcorn.soundcloudclone.security.CurrentUserContext;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

// Uses TrackMapper để map PlaylistTrack -> TrackItemResponse
@Mapper(componentModel = "spring", uses = { UserMapper.class, TrackMapper.class })
public abstract class PlaylistMapper {

    @Autowired
    protected CurrentUserContext ctx;

    @Mapping(target = "user", source = "creator")
    @Mapping(target = "tracks", source = "joinTracks")
    @Mapping(target = "liked", expression = "java(ctx.isPlaylistLiked(playlist.getId()))")
    public abstract PlaylistResponse toPlaylistResponse(Playlist playlist);

    // Method này không cần check like track nên không cần Context
    @Mapping(target = "name", source = "name")
    @Mapping(target = "user", source = "creator")
    @Mapping(target = "trackCount", expression = "java(playlist.getJoinTracks() != null ? playlist.getJoinTracks().size() : 0)")
    @Mapping(target = "liked", expression = "java(ctx.isPlaylistLiked(playlist.getId()))")
    @Mapping(target = "imageUrl", source = "playlist", qualifiedByName = "getPlaylistCoverImage")
    public abstract PlaylistSummaryResponse toPlaylistSummaryResponse(Playlist playlist);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void patchUpdate(@MappingTarget Playlist playlist, PlaylistUpdateRequest request);

    @Named("getPlaylistCoverImage")
    public String getPlaylistCoverImage(Playlist playlist) {
        if (playlist.getJoinTracks() == null || playlist.getJoinTracks().isEmpty()) {
            return null; // Hoặc default image
        }
        // Giả sử lấy ảnh của track đầu tiên
        return playlist.getJoinTracks().get(0).getTrack().getImageUpload().getUrl();
    }
}