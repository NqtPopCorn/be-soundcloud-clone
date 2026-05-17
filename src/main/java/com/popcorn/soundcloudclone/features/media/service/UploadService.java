package com.popcorn.soundcloudclone.features.media.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {

    String upload(MultipartFile file, String folder, String type);

    /*
     * Update if oldUrl exists and not null, otherwise upload
     */
    String upsert(MultipartFile file, String oldUrl, String folder, String type);

    void delete(String url);
}
