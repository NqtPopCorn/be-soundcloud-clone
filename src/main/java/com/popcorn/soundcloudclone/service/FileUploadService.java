package com.popcorn.soundcloudclone.service;

import com.popcorn.soundcloudclone.domain.entity.FileUpload;
import org.springframework.web.multipart.MultipartFile;


public interface FileUploadService {
    /**
     * tạo mới file trong bộ nhớ và lưu vào db
     */
    FileUpload storeFile(MultipartFile file, FileUpload.FileType fileType);

    /**
     * Replace file trong bộ nhớ mà không thay đổi đường dẫn nên sẽ không thay đổi db
     */
    boolean replaceFile(FileUpload fileUpload, MultipartFile file);

    String getFilePath(Integer id);

    boolean deleteFile(Integer id);
}
