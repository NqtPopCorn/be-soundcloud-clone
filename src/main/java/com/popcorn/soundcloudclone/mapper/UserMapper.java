package com.popcorn.soundcloudclone.mapper;

import com.popcorn.soundcloudclone.domain.dto.user.*;
import com.popcorn.soundcloudclone.domain.entity.Playlist;
import com.popcorn.soundcloudclone.domain.entity.PlaylistTrack;
import com.popcorn.soundcloudclone.domain.entity.User;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { SharedQualifier.class })
public interface UserMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", source = "password", qualifiedByName = "hashPassword")
    User toUser(UserCreationRequest request);

    @Mapping(target = "avatarUrl", source = "avatarUpload.url")
    @Mapping(target = "backgroundUrl", source = "backgroundUpload.url")
    @Mapping(target = "role", source = ".", qualifiedByName = "getRoleName")
    UserResponse toUserResponse(User user);

    @Mapping(target = "avatarUrl", source = "avatarUpload.url")
    UserSummaryResponse toUserSummaryResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", source = "password", qualifiedByName = "hashPassword")
    @Mapping(target = "avatarUpload", ignore = true)
    @Mapping(target = "backgroundUpload", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    // @BeanMapping(nullValuePropertyMappingStrategy =
    // NullValuePropertyMappingStrategy.IGNORE)
    // @Mapping(target = "password", source = "password", qualifiedByName =
    // "hashPassword")
    // @Mapping(target = "avatarUpload", ignore = true)
    // @Mapping(target = "backgroundUpload", ignore = true)
    // void updateUser(@MappingTarget User user, AdminCreationUserRequest request);

    @Named("getFirstTrackImage")
    default String getFirstTrackImage(List<PlaylistTrack> tracks) {
        if (tracks == null || tracks.isEmpty())
            return null;
        return tracks.get(0).getTrack().getImageUpload().getUrl();
    }

    // TODO: map user links

}
