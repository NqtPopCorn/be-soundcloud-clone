package com.popcorn.soundcloudclone.controller;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumCreationRequest;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumUpdateRequest;
import com.popcorn.soundcloudclone.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/album")
public class AlbumController {
    private final AlbumService albumService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AlbumResponse>>> getPage(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sortType) {
        boolean asc = "asc".equalsIgnoreCase(sortType);
        var pageResponse = albumService.findByKeyword(keyword, page, size, asc);
        var body = ApiResponse.<PageResponse<AlbumResponse>>builder()
                .code(1000)
                .message("Success")
                .result(pageResponse)
                .build();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AlbumResponse>> getById(@PathVariable int id) {
        var response = albumService.getById(id);
        var body = ApiResponse.<AlbumResponse>builder()
                .code(1000)
                .message("Album found")
                .result(response)
                .build();
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addAlbum(Authentication auth, @RequestBody @Valid AlbumCreationRequest request) {
        int userId = Integer.parseInt(auth.getName());
        albumService.create(userId, request);
        return ResponseEntity.ok(ApiResponse.builder()
                        .code(1000)
                        .message("Create successfully")
                .build());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> updateAlbum(@PathVariable int id, @ModelAttribute @Valid AlbumUpdateRequest request) {
        albumService.updateAlbum(id, request);
        return ResponseEntity.ok(ApiResponse.builder()
                .code(1000)
                .message("Update successfully")
                .build());
    }

    @PutMapping("/{albumId}/add-track")
    public ResponseEntity<ApiResponse> addTrackAlbum(@PathVariable int albumId, @RequestParam List<Integer> trackIds) {
        albumService.addTracksToAlbum(albumId, trackIds);
        return ResponseEntity.ok(ApiResponse.builder()
                    .code(1000)
                    .message("Add track successfully")
                .build());
    }

    @PutMapping("/{albumId}/tracks")
    public ResponseEntity<ApiResponse> updateAlbumTracks(@PathVariable int albumId, @RequestParam List<Integer> trackIds) {
        albumService.updateAlbumTracks(albumId, trackIds);
        return ResponseEntity.ok(ApiResponse.builder()
                .code(1000)
                .message("Update album tracks successfully")
                .build());
    }

    @PutMapping("/{albumId}/delete-image")
    public ResponseEntity<ApiResponse> deleteImageAlbum(@PathVariable int albumId) {
        albumService.deleteAlbumImage(albumId);
        return ResponseEntity.ok(ApiResponse.builder()
                .code(1000)
                .message("Update album tracks successfully")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteAlbum(@PathVariable int id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .code(1000)
                .message("Delete album successfully")
                .build());
    }
}
