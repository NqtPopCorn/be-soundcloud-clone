package com.popcorn.soundcloudclone.features.user.controller;

import com.popcorn.soundcloudclone.common.response.ApiResponse;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.track.dto.response.TrackResponse;
import com.popcorn.soundcloudclone.features.track.service.TrackService;
import com.popcorn.soundcloudclone.features.user.dto.request.LikeResquest;
import com.popcorn.soundcloudclone.features.user.service.FavoriteTrackService;

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
@RequestMapping("/likes/tracks")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class FavoriteTrackController {
        private final FavoriteTrackService favoriteTrackService;
        private final TrackService trackService;

        @GetMapping("")
        @PreAuthorize("isAuthenticated() && #userDetails != null")
        public ResponseEntity<ApiResponse<PageResponse<TrackResponse>>> getLikedTracks(
                        @AuthenticationPrincipal MyUserDetails userDetails,
                        @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
                var results = favoriteTrackService.getLikedTracks(userDetails.getUserId(), pageable);

                var body = ApiResponse.<PageResponse<TrackResponse>>builder()
                                .statusCode(200)
                                .message("Success")
                                .result(results)
                                .build();
                return ResponseEntity.ok(body);
        }

        @PostMapping("/{trackId}")
        @PreAuthorize("isAuthenticated() && #userDetails != null")
        public ResponseEntity<ApiResponse> togglelikeTrack(
                        @Valid @RequestBody LikeResquest dto,
                        @AuthenticationPrincipal MyUserDetails userDetails) {
                favoriteTrackService.toggleLikeTrack(userDetails.getUserId(), dto.getObjectId(), dto.getLiked());

                var body = ApiResponse.builder()
                                .statusCode(200)
                                .message("Success")
                                .build();
                return ResponseEntity.ok(body);
        }

        @GetMapping("/recent-plays")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<ApiResponse<PageResponse<TrackResponse>>> playTrack(
                        @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                        @AuthenticationPrincipal MyUserDetails userDetails) {
                var res = trackService.getPageRecentPlay(userDetails.getUserId(), pageable);
                return ResponseEntity.ok(ApiResponse.<PageResponse<TrackResponse>>builder()
                                .result(res)
                                .statusCode(200)
                                .message("Success")
                                .build());
        }

}
