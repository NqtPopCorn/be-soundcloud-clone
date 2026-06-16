package com.popcorn.soundcloudclone.features.upgradeartist.mapper;

import com.popcorn.soundcloudclone.features.upgradeartist.dto.ArtistUpgradeRequestResponse;
import com.popcorn.soundcloudclone.features.upgradeartist.entity.ArtistUpgradeRequest;
import com.popcorn.soundcloudclone.features.users.mapper.UserMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface ArtistUpgradeRequestMapper {
    ArtistUpgradeRequestResponse toResponse(ArtistUpgradeRequest request);
}
