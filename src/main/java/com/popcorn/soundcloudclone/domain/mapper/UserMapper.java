package com.popcorn.soundcloudclone.domain.mapper;

import com.popcorn.soundcloudclone.domain.dto.user.UserCreationRequest;
import com.popcorn.soundcloudclone.domain.dto.user.UserResponse;
import com.popcorn.soundcloudclone.domain.dto.user.UserSummaryResponse;
import com.popcorn.soundcloudclone.domain.dto.user.UserUpdateRequest;
import com.popcorn.soundcloudclone.dto.user.*;
import com.popcorn.soundcloudclone.domain.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {SharedQualifier.class})
public interface UserMapper {

    User toUser(UserCreationRequest request);

    @Mapping(target = "avatarUrl", source = "avatarUpload", qualifiedByName = "getImageUrl")
    @Mapping(target = "backgroundUrl", source = "backgroundUpload", qualifiedByName = "getImageUrl")
    @Mapping(target = "role", source = ".", qualifiedByName = "getRoleName")
    UserResponse toUserResponse(User user);

    @Mapping(target = "avatarUrl", source = "avatarUpload", qualifiedByName = "getImageUrl")
    UserSummaryResponse toUserSummaryResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", source = "password", qualifiedByName = "hashPassword")
    @Mapping(target = "avatarUpload", ignore = true)
    @Mapping(target = "backgroundUpload", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    // TODO: map user link

}

