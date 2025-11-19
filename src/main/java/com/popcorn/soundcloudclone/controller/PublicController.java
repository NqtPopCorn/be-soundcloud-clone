package com.popcorn.soundcloudclone.controller;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.track.TrackCreationRequest;
import com.popcorn.soundcloudclone.domain.dto.track.TrackResponse;
import com.popcorn.soundcloudclone.security.MyUserDetails;
import com.popcorn.soundcloudclone.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {
    private final FileUploadService fileUploadService;

    @GetMapping("/resource/{fileName}")
    public ResponseEntity<Resource> getImageFile(@PathVariable("fileName") String fileName) throws IOException {

        Path filePath = Paths.get(fileUploadService.getFilePath(fileName));
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new FileNotFoundException("File not found: " + resource.getFilename());
        }

//        String contentType = Files.probeContentType(filePath);
//        "image/*"

        return ResponseEntity.ok()
                .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(resource);
    }

    @GetMapping("/resource/stream/{fileName}")
    public ResponseEntity<ResourceRegion> streamAudio(
            @PathVariable("fileName") String fileName,
            @RequestHeader HttpHeaders headers,
            @RequestParam String token) throws IOException {
        // neu bai hat private thi yeu cau xac thuc token
//        var valid = authService.introspect(token);
//        var userDetails = userDetailsService.loadUserByUsername(valid.getUsername());
//        if(!valid.isValid() || !userSecurity.hasTrackPermit(trackId, userDetails)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//        }



        Path path = Paths.get(fileUploadService.getFilePath(fileName));
        FileSystemResource audio = new FileSystemResource(path);
        long contentLength = audio.contentLength();

        // Tìm byte range client yêu cầu
        HttpRange range = headers.getRange().isEmpty()
                ? HttpRange.createByteRange(0, contentLength - 1)
                : headers.getRange().get(0);

        long start = range.getRangeStart(contentLength);
        long end   = range.getRangeEnd(contentLength);
        long rangeLength = Math.min(1_000_000, end - start + 1); // 1 MB chunk

        ResourceRegion region = new ResourceRegion(audio, start, rangeLength);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(audio).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }

}
