package com.popcorn.soundcloudclone.controller;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.track.TrackCreationRequest;
import com.popcorn.soundcloudclone.domain.dto.track.TrackResponse;
import com.popcorn.soundcloudclone.domain.service.FileUploadService;
import com.popcorn.soundcloudclone.security.MyUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {
    private final FileUploadService fileUploadService;

    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> getImageFile(@PathVariable("fileName") String fileName) throws IOException {

        Path filePath = Paths.get(fileUploadService.getFilePath(fileName));
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new FileNotFoundException("File not found: " + filePath.toString());
        }

        var contentType = MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(contentType)
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .body(resource);
    }

    @GetMapping("/audio/{fileName}")
    public ResponseEntity<Resource> streamAudio(
            @PathVariable("fileName") String fileName,
            @RequestHeader HttpHeaders headers) throws IOException {

        Path path = Paths.get(fileUploadService.getFilePath(fileName));
        FileSystemResource audio = new FileSystemResource(path);

        // chunk streaming example
        // long contentLength = audio.contentLength();
        // Tìm byte range client yêu cầu
        // HttpRange range = headers.getRange().isEmpty()
        // ? HttpRange.createByteRange(0, contentLength - 1)
        // : headers.getRange().get(0);

        // long start = range.getRangeStart(contentLength);
        // long end = range.getRangeEnd(contentLength);
        // long rangeLength = Math.min(1_000_000, end - start + 1); // 1 MB chunk

        // ResourceRegion region = new ResourceRegion(audio, start, rangeLength);
        // return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
        // body(region);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaTypeFactory.getMediaType(audio).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(15)))
                .body(audio);
    }

}
