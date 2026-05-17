package com.popcorn.soundcloudclone.features.user.service;

import com.popcorn.soundcloudclone.features.users.dto.request.UserUpdateRequest;
import com.popcorn.soundcloudclone.features.users.dto.response.UserResponse;

public interface UserService {

    UserResponse getUserProfileById(int id);

    UserResponse patchUpdateUser(int userId, UserUpdateRequest request);

}
