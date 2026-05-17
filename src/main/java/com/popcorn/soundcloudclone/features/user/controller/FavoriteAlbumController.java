package com.popcorn.soundcloudclone.features.user.controller;

import com.popcorn.soundcloudclone.common.response.ApiResponse;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.album.dto.response.AlbumSummaryResponse;
import com.popcorn.soundcloudclone.features.user.dto.request.LikeResquest;
import com.popcorn.soundcloudclone.features.user.service.FavoriteAlbumService;

import jakarta.validation.Valid;

import com.popcorn.soundcloudclone.common.security.MyUserDetails;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/likes/albums")
@RequiredArgsConstructor
public class FavoriteAlbumController {
        private final FavoriteAlbumService favoriteService;

        @GetMapping
        @PreAuthorize("isAuthenticated() && #userDetails != null")
        public ResponseEntity<ApiResponse<PageResponse<AlbumSummaryResponse>>> getPage(
                        @AuthenticationPrincipal MyUserDetails userDetails,
                        @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
                var results = favoriteService.getLikedAlbums(userDetails.getUserId(), pageable);
                var body = ApiResponse.<PageResponse<AlbumSummaryResponse>>builder()
                                .statusCode(200)
                                .message("Success")
                                .result(results)
                                .build();
                return ResponseEntity.ok(body);
        }

        @PostMapping("/{albumId}")
        @PreAuthorize("isAuthenticated() && #userDetails != null")
        public ResponseEntity<ApiResponse> likeAlbum(
                        @Valid @RequestBody LikeResquest dto,
                        @AuthenticationPrincipal MyUserDetails userDetails) {
                favoriteService.toggleLikeAlbum(userDetails.getUserId(), dto.getObjectId(), dto.getLiked());
                var body = ApiResponse.builder()
                                .statusCode(200)
                                .message("Success")
                                .build();
                return ResponseEntity.ok(body);
        }
}
