package com.popcorn.soundcloudclone.domain.mapper;

import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistResponse;
import com.popcorn.soundcloudclone.domain.entity.Playlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, TrackMapper.class})
//@RequiredArgsConstructor
public abstract class PlaylistMapper {

    @Mapping(target = "user", source = "creator")
    @Mapping(target = "tracks", source = "tracks")
    public abstract PlaylistResponse toPlaylistResponse(Playlist playlist);

}
