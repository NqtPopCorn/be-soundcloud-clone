package com.popcorn.soundcloudclone.features.user.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.common.exception.ErrorCode;
import com.popcorn.soundcloudclone.features.media.service.UploadService;
import com.popcorn.soundcloudclone.features.user.service.ProfileMediaService;
import com.popcorn.soundcloudclone.features.user.service.UserService;
import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
public class ProfileMediaServiceImpl implements ProfileMediaService {

    private final UploadService uploadService;
    private final UserRepository userRepository;

    private final String FOLDER_USERS = "soundcloud/users/";

    @Override
    @Transactional
    public void updateAvatar(int userId, MultipartFile upload) {
        var user = findUserByIdOrThrow(userId);
        String existing = user.getAvatarUrl();
        // file null thi xoa, khac null thi update
        // update neu file non null
        var newAvatar = updateImageIfNonNull(upload, existing, userId);
        user.setAvatarUrl(newAvatar);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateBackgroundImage(int userId, MultipartFile upload) {
        var user = findUserByIdOrThrow(userId);
        String existing = user.getBackgroundUrl();
        // update neu file non null
        var newBackground = updateImageIfNonNull(upload, existing, userId);
        user.setBackgroundUrl(newBackground);
        userRepository.save(user);
    }

    private String updateImageIfNonNull(MultipartFile upload, String existing, Integer userId) {
        if (upload != null) {
            if (existing != null) {
                return uploadService.upsert(upload, existing, FOLDER_USERS + userId, "image");
            } else {
                return uploadService.upload(upload, FOLDER_USERS + userId, "image");
            }
        }
        return existing;
    }

    @Override
    @Transactional
    public void deleteAvatar(int userId) {
        var user = findUserByIdOrThrow(userId);
        var avatar = user.getAvatarUrl();
        if (avatar != null) {
            uploadService.delete(avatar);
        }
        user.setAvatarUrl(null);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteBackgroundImage(int userId) {
        var user = findUserByIdOrThrow(userId);
        var background = user.getBackgroundUrl();
        if (background != null) {
            uploadService.delete(background);
        }
        user.setBackgroundUrl(null);
        userRepository.save(user);
    }

    private User findUserByIdOrThrow(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("User not found with id: " + id, ErrorCode.NOT_FOUND));
    }

}
