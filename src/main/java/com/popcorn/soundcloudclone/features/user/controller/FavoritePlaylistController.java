package com.popcorn.soundcloudclone.features.user.controller;

import com.popcorn.soundcloudclone.common.response.ApiResponse;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.playlist.dto.request.PlaylistFilterRequest;
import com.popcorn.soundcloudclone.features.playlist.dto.response.PlaylistResponse;
import com.popcorn.soundcloudclone.features.playlist.dto.response.PlaylistSummaryResponse;
import com.popcorn.soundcloudclone.features.playlist.service.PlaylistService;
import com.popcorn.soundcloudclone.features.user.dto.request.LikeResquest;
import com.popcorn.soundcloudclone.features.user.service.FavoritePlaylistService;

import jakarta.validation.Valid;

import com.popcorn.soundcloudclone.common.security.MyUserDetails;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/likes/playlists")
@RequiredArgsConstructor
public class FavoritePlaylistController {
        private final FavoritePlaylistService favoritePlaylistService;

        @GetMapping("")
        @PreAuthorize("isAuthenticated() && #userDetails != null")
        public ResponseEntity<ApiResponse<PageResponse<PlaylistSummaryResponse>>> getLikedPlaylists(
                        @AuthenticationPrincipal MyUserDetails userDetails,
                        @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable

        ) {
                var results = favoritePlaylistService.getLikedPlaylists(userDetails.getUserId(), pageable);
                var body = ApiResponse.<PageResponse<PlaylistSummaryResponse>>builder()
                                .statusCode(200)
                                .message("Success")
                                .result(results)
                                .build();
                return ResponseEntity.ok(body);
        }

        @PostMapping()
        @PreAuthorize("isAuthenticated() && #userDetails != null")
        public ResponseEntity<ApiResponse> likePlaylist(
                        @Valid @RequestBody LikeResquest dto,
                        @AuthenticationPrincipal MyUserDetails userDetails) {
                favoritePlaylistService.toggleLikePlaylist(userDetails.getUserId(), dto.getObjectId(), dto.getLiked());
                var body = ApiResponse.builder()
                                .statusCode(200)
                                .message("Success")
                                .build();
                return ResponseEntity.ok(body);
        }
}
