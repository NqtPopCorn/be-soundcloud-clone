package com.popcorn.soundcloudclone.repository;

import com.popcorn.soundcloudclone.domain.entity.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileUploadRepository extends JpaRepository<FileUpload, Integer> {
    Optional<FileUpload> findByFileName(String fileName);
}
