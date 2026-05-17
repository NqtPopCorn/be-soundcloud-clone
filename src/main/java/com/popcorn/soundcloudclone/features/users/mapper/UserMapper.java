package com.popcorn.soundcloudclone.features.users.mapper;

import com.popcorn.soundcloudclone.common.utils.SharedQualifier;
import com.popcorn.soundcloudclone.features.users.dto.request.UserCreationRequest;
import com.popcorn.soundcloudclone.features.users.dto.request.UserUpdateRequest;
import com.popcorn.soundcloudclone.features.users.dto.response.UserResponse;
import com.popcorn.soundcloudclone.features.users.dto.response.UserSummaryResponse;
import com.popcorn.soundcloudclone.features.users.entity.User;

import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { SharedQualifier.class })
public interface UserMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", source = "password", qualifiedByName = "hashPassword")
    User toUser(UserCreationRequest request);

    @Mapping(target = "role", source = ".", qualifiedByName = "getRoleName")
    UserResponse toUserResponse(User user);

    UserSummaryResponse toUserSummaryResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", source = "password", qualifiedByName = "hashPassword")
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

}
