package com.popcorn.soundcloudclone.features.media.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.popcorn.soundcloudclone.features.media.service.UploadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryUploadService implements UploadService {

    private final Cloudinary cloudinary;

    private void validateMediaType(String type) {
        if (type == null || type.isBlank())
            throw new IllegalArgumentException("Media type cannot be null or empty.");
        if (!type.equals("image") && !type.equals("video"))
            throw new IllegalArgumentException("Invalid media type. Must be 'image' or 'video'.");
    }

    @Override
    public String upload(MultipartFile file, String folder, String type) {
        if (file == null || file.isEmpty())
            return null;

        // map theo quy ước của cloudinary
        if (type.equals("audio")) {
            type = "video";
        }

        validateMediaType(type);

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", type));
            return (String) result.get("secure_url");
        } catch (IOException e) {
            log.error("Cloudinary image upload failed for folder={}", folder, e);
            throw new RuntimeException("Image upload failed", e);
        }
    }

    @Override
    public String upsert(MultipartFile file, String oldUrl, String folder, String type) {
        if (file == null || file.isEmpty())
            return oldUrl;

        // map theo quy ước của cloudinary
        if (type.equals("audio")) {
            type = "video";
        }

        validateMediaType(type);

        if (oldUrl == null || oldUrl.isBlank()) {
            return upload(file, folder, type);
        }

        try {
            String publicId = extractPublicId(oldUrl);
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "overwrite", true,
                            "invalidate", true,
                            "resource_type", type));
            return (String) result.get("secure_url");
        } catch (IOException e) {
            log.error("Cloudinary image update failed for url={}", oldUrl, e);
            throw new RuntimeException("Image update failed", e);
        }
    }

    @Override
    public void delete(String url) {
        if (url == null || url.isBlank())
            return;

        try {
            String publicId = extractPublicId(url);
            if (publicId == null) {
                return;
            }

            // Dựa vào cấu trúc URL của Cloudinary để xác định resource_type (audio sử dụng
            // hệ thống video của Cloudinary)
            boolean isAudio = url.contains("/video/upload/");
            String resourceType = isAudio ? "video" : "image";

            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", resourceType));

            log.info("Deleted asset successfully: publicId={}, resource_type={}", publicId, resourceType);
        } catch (IOException e) {
            log.error("Cloudinary delete failed for url={}", url, e);
            throw new RuntimeException("Asset deletion failed", e);
        }
    }

    /**
     * Extract Cloudinary public_id from a secure URL.
     * e.g. "https://res.cloudinary.com/demo/image/upload/v123456789/tracks/abc.jpg"
     * → "tracks/abc"
     */
    private static String extractPublicId(String cloudinaryUrl) {
        if (cloudinaryUrl == null || cloudinaryUrl.isBlank())
            return null;
        int uploadIdx = cloudinaryUrl.indexOf("/upload/");
        if (uploadIdx == -1)
            return cloudinaryUrl;
        String afterUpload = cloudinaryUrl.substring(uploadIdx + 8);
        // strip version token if present (v<digits>/)
        afterUpload = afterUpload.replaceFirst("^v\\d+/", "");
        int dotIdx = afterUpload.lastIndexOf('.');
        return dotIdx != -1 ? afterUpload.substring(0, dotIdx) : afterUpload;
    }
}