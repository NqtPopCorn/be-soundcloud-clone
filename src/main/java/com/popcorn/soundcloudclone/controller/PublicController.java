package com.popcorn.soundcloudclone.controller;

import com.popcorn.soundcloudclone.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
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

    @GetMapping("/resource/{id}")
    public ResponseEntity<Resource> getImageFile(@PathVariable("id") Integer id) throws IOException {

        Path filePath = Paths.get(fileUploadService.getFilePath(id));
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
}
