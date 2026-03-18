package com.popcorn.soundcloudclone.domain.service;

import com.popcorn.soundcloudclone.domain.dto.user.*;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserResponse createRequest(UserCreationRequest request);

    UserResponse createAdminRequest(AdminCreationUserRequest request);

    PageResponse<UserResponse> getPageUsers(String keyword, Pageable pageable);

    UserResponse getUserProfileById(int id);

    UserResponse getUserProfileByUsername(String username);

    UserResponse patchUpdateUser(int userId, UserUpdateRequest request);

    void updateAvatar(int userId, MultipartFile file);

    void updateBackgroundImage(int userId, MultipartFile file);

    /**
     * update user but include update role
     */
    UserResponse adminUpdateUser(int userId, AdminUpdateUserRequest request);

    void deleteUser(int userId);

    void deleteAvatar(int userId);

    void deleteBackgroundImage(int userId);
}
