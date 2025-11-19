package com.popcorn.soundcloudclone.service.impl;

import com.popcorn.soundcloudclone.domain.entity.FileUpload;
import com.popcorn.soundcloudclone.exception.BadRequestException;
import com.popcorn.soundcloudclone.exception.ErrorCode;
import com.popcorn.soundcloudclone.repository.FileUploadRepository;
import com.popcorn.soundcloudclone.service.FileUploadService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${app.upload-dir}")
    protected String uploadDir;
    @Value("${app.image-base-url}")
    protected String imageUploadBaseUrl;
    @Value("${app.audio-base-url}")
    protected String audioUploadBaseUrl;

    private Path rootDir;
    private final FileUploadRepository fileUploadRepository;


    @PostConstruct
    public void init() throws IOException {
        rootDir = Paths.get(uploadDir);
        if (!Files.exists(rootDir)) {
            Files.createDirectories(rootDir);
        }
    }

    @Override
    @Transactional
    public FileUpload storeFile(MultipartFile file, FileUpload.FileType fileType) {
        if (file == null || file.isEmpty()) return null;
//        String baseUrl = fileType == FileUpload.FileType.audio ? audioUploadBaseUrl : imageUploadBaseUrl;

        try {
            String uniqueName = generateFilename(file);
            Path destination = rootDir.resolve(fileType.name()).resolve(uniqueName);
            String url = (fileType == FileUpload.FileType.audio ? audioUploadBaseUrl : imageUploadBaseUrl) + uniqueName;
            var fileUpload = FileUpload.builder()
                    .originalName(file.getOriginalFilename())
                    .fileName(uniqueName)
                    .filePath(destination.toString())
                    .fileType(fileType)
                    .size(file.getSize())
                    .url(url)
                    .build();
            fileUploadRepository.save(fileUpload);

            // save file sau cung de co the call back neu khong luu vao duoc db
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return fileUpload;
        } catch (IOException e) {
            throw new RuntimeException("File saving failed", e);
        }
    }

    @Override
    public boolean replaceFile(FileUpload fileUpload, MultipartFile file) {
        if (file == null || file.isEmpty()) return false;
        try {
            Path destination = Paths.get(fileUpload.getFilePath());
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("File saving failed", e);
        }
    }

    @Override
    public String getFilePath(String fileName) {
        return fileUploadRepository.findByFileName(fileName)
                .orElseThrow(() -> new BadRequestException(ErrorCode.NOT_FOUND))
                .getFilePath();
    }

    @Override
    @Transactional
    public boolean  deleteFile(Integer id) {
        FileUpload fileUpload = fileUploadRepository.findById(id).orElseThrow(() -> new BadRequestException(ErrorCode.NOT_FOUND));
        if (fileUpload == null) return false;
        try {
            fileUploadRepository.delete(fileUpload);
            Path path = Paths.get(fileUpload.getFilePath());
            Files.delete(path);
            return true;
        } catch (Exception e) {
            log.error("File deletion failed", e);
            throw new RuntimeException("File deletion failed", e);
        }
    }

    private String generateFilename(MultipartFile file) {
        String originalFileName = Optional.ofNullable(file.getOriginalFilename()).orElse("file");

        // Lấy extension
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex != -1) {
            extension = originalFileName.substring(dotIndex); // includes dot
            originalFileName = originalFileName.substring(0, dotIndex);
        }

        // Rút gọn + sanitize
        int minLength = Math.min(originalFileName.length(), 20);
        String sanitized = originalFileName.trim()
                .replaceAll("[^a-zA-Z0-9]", "-")
                .substring(0, minLength);

        // Gắn timestamp mili-giây
        return sanitized + "_" + Instant.now().toEpochMilli() + extension.toLowerCase();
    }



}
