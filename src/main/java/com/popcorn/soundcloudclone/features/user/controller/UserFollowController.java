package com.popcorn.soundcloudclone.features.user.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.popcorn.soundcloudclone.common.response.ApiResponse;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.common.security.MyUserDetails;
import com.popcorn.soundcloudclone.features.user.service.UserFollowService;
import com.popcorn.soundcloudclone.features.users.dto.response.UserResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user/follows")
@PreAuthorize("isAuthenticated() && #userDetails != null")
@RequiredArgsConstructor
public class UserFollowController {
        private final UserFollowService userFollowService;

        @PostMapping("/{targetId}")
        public ResponseEntity<ApiResponse<Void>> followUser(
                        @PathVariable("targetId") int id,
                        @AuthenticationPrincipal MyUserDetails userDetails) {
                userFollowService.followUser(userDetails.getUserId(), id);
                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .statusCode(200)
                                .message("Success")
                                .build());
        }

        @DeleteMapping("/{targetId}")
        // @PreAuthorize("isAuthenticated() && #userDetails != null")
        public ResponseEntity<ApiResponse<Void>> unfollowUser(
                        @PathVariable("targetId") int id,
                        @AuthenticationPrincipal MyUserDetails userDetails) {
                userFollowService.unfollowUser(userDetails.getUserId(), id);
                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .statusCode(200)
                                .message("Success")
                                .build());
        }

        // @GetMapping("/followers")
        // public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getFollowers(
        // @PathVariable int id,
        // @PageableDefault Pageable pageable) {
        // var result = userFollowService.getFollowers(id, pageable);
        // return ResponseEntity.ok(ApiResponse.<PageResponse<UserResponse>>builder()
        // .statusCode(200)
        // .message("Success")
        // .result(result)
        // .build());
        // }

        @GetMapping()
        // @PreAuthorize("isAuthenticated() && #userDetails != null")
        public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getFollowing(
                        @AuthenticationPrincipal MyUserDetails userDetails,
                        @PageableDefault Pageable pageable) {
                var result = userFollowService.getFollowingUsers(userDetails.getUserId(), pageable);
                return ResponseEntity.ok(ApiResponse.<PageResponse<UserResponse>>builder()
                                .statusCode(200)
                                .message("Success")
                                .result(result)
                                .build());
        }
}
