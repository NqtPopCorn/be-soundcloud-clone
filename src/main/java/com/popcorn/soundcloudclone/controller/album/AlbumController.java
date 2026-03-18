package com.popcorn.soundcloudclone.controller.album;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumCreationRequest;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumFilterRequestDto;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumUpdateRequest;
import com.popcorn.soundcloudclone.domain.service.AlbumService;
import com.popcorn.soundcloudclone.domain.service.FavoritePlaylistService;
import com.popcorn.soundcloudclone.security.MyUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/albums")
public class AlbumController {
        private final AlbumService albumService;
        private final FavoritePlaylistService favoritePlaylistService;

        @GetMapping
        public ResponseEntity<ApiResponse<PageResponse<AlbumResponse>>> getPage(
                        @ModelAttribute AlbumFilterRequestDto dto,
                        @PageableDefault Pageable pageable) {
                var pageResponse = albumService.findByFilter(dto, pageable);
                var body = ApiResponse.<PageResponse<AlbumResponse>>builder()
                                .statusCode(200)
                                .message("Success")
                                .result(pageResponse)
                                .build();
                return ResponseEntity.ok(body);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<AlbumResponse>> getById(@PathVariable int id,
                        @AuthenticationPrincipal MyUserDetails user) {
                Integer userId = user == null ? null : user.getUserId();
                var response = albumService.getById(id, userId);
                var body = ApiResponse.<AlbumResponse>builder()
                                .statusCode(200)
                                .message("Album found")
                                .result(response)
                                .build();
                return ResponseEntity.ok(body);
        }

        @PostMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'ARTIST')")
        public ResponseEntity<ApiResponse<AlbumResponse>> addAlbum(@AuthenticationPrincipal MyUserDetails user,
                        @RequestBody @Valid AlbumCreationRequest request) {
                Integer userId = user.getUserId();
                var res = albumService.create(userId, request);
                return ResponseEntity.ok(ApiResponse.<AlbumResponse>builder()
                                .statusCode(200)
                                .message("Create successfully")
                                .result(res)
                                .build());
        }

        @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        // todo: check owner
        public ResponseEntity<ApiResponse> updateAlbum(@PathVariable int id,
                        @ModelAttribute @Valid AlbumUpdateRequest request) {
                albumService.updateAlbum(id, request);
                return ResponseEntity.ok(ApiResponse.builder()
                                .statusCode(200)
                                .message("Update successfully")
                                .build());
        }

        @PostMapping("/{albumId}/tracks")
        // todo: check owner
        public ResponseEntity<ApiResponse> addTrackAlbum(@PathVariable int albumId,
                        @RequestBody List<Integer> trackIds) {
                albumService.addTracksToAlbum(albumId, trackIds);
                return ResponseEntity.ok(ApiResponse.builder()
                                .statusCode(200)
                                .message("Add track successfully")
                                .build());
        }

        @PutMapping("/{albumId}/tracks")
        // todo: check owner
        // sort track
        public ResponseEntity<ApiResponse> updateAlbumTracks(@PathVariable int albumId,
                        @RequestParam List<Integer> trackIds) {
                albumService.updateAlbumTracks(albumId, trackIds);
                return ResponseEntity.ok(ApiResponse.builder()
                                .statusCode(200)
                                .message("Update album tracks successfully")
                                .build());
        }

        @DeleteMapping("/{id}")
        // todo: check owner, admin
        public ResponseEntity<ApiResponse> deleteAlbum(@PathVariable int id) {
                albumService.deleteAlbum(id);
                return ResponseEntity.ok(ApiResponse.builder()
                                .statusCode(200)
                                .message("Delete album successfully")
                                .build());
        }

        // TODO: image cover?
}
