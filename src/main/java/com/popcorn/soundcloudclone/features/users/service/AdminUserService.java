package com.popcorn.soundcloudclone.features.users.service;

import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.users.dto.request.AdminCreationUserRequest;
import com.popcorn.soundcloudclone.features.users.dto.request.AdminUpdateUserRequest;
import com.popcorn.soundcloudclone.features.users.dto.response.UserResponse;

import org.springframework.data.domain.Pageable;

public interface AdminUserService {

    PageResponse<UserResponse> getPageUsers(String keyword, Pageable pageable);

    UserResponse createUser(AdminCreationUserRequest request);

    UserResponse getUserById(int userId);

    UserResponse getUserByUsername(String username);

    UserResponse updateUser(int userId, AdminUpdateUserRequest request);

    void deleteUser(int userId);

    // void banUser(int userId);

    // void unbanUser(int userId);

}