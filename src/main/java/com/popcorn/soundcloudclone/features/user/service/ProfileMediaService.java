package com.popcorn.soundcloudclone.features.user.service;

import org.springframework.web.multipart.MultipartFile;

public interface ProfileMediaService {
    void updateAvatar(int userId, MultipartFile file);

    void updateBackgroundImage(int userId, MultipartFile file);

    void deleteAvatar(int userId);

    void deleteBackgroundImage(int userId);
}
