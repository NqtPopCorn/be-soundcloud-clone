package com.popcorn.soundcloudclone.service;

import com.popcorn.soundcloudclone.domain.dto.user.*;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserResponse createRequest(UserCreationRequest request);

    /**
     * Update user without role
     */
    UserResponse createAdminRequest(AdminCreationUserRequest request);

    PageResponse<UserResponse> getPageUsers(String keyword, int page, int size, String sort);

    UserResponse userGetInfo(int id);
    UserResponse userGetInfoByUsername(String username);

    UserResponse updateUser(int userId, UserUpdateRequest request);

    void updateAvatar(int userId, MultipartFile file);

    void updateBackgroundImage(int userId, MultipartFile file);

    void followUser(int userId, int artistId);

    void unFollowUser(int userId, int artistId);

    /**
     * update user but include update role
     */
    UserResponse adminUpdateUser(int userId, AdminUpdateUserRequest request);

    void deleteUser(int userId);

    void deleteAvatar(int userId);
    void deleteBackgroundImage(int userId);
}
