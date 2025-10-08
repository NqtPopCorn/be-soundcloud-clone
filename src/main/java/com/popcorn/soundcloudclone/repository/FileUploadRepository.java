package com.popcorn.soundcloudclone.repository;

import com.popcorn.soundcloudclone.domain.entity.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileUploadRepository extends JpaRepository<FileUpload, Integer> {

}
