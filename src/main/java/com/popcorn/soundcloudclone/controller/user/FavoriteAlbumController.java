package com.popcorn.soundcloudclone.controller.user;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.album.AlbumSummaryResponse;
import com.popcorn.soundcloudclone.domain.service.FavoriteAlbumService;
import com.popcorn.soundcloudclone.security.MyUserDetails;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/users/me/favorite-albums")
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

    @PostMapping("/like/{albumId}")
    @PreAuthorize("isAuthenticated() && #userDetails != null")
    public ResponseEntity<ApiResponse> likeAlbum(

            @PathVariable Integer albumId,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        favoriteService.likeAlbum(userDetails.getUserId(), albumId);
        var body = ApiResponse.builder()
                .statusCode(200)
                .message("Success")
                .build();
        return ResponseEntity.ok(body);
    }

    @PostMapping("/unlike/{albumId}")
    @PreAuthorize("isAuthenticated() && #userDetails != null")
    public ResponseEntity<ApiResponse> unlikeAlbum(
            @PathVariable Integer albumId,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        favoriteService.unlikeAlbum(userDetails.getUserId(), albumId);
        var body = ApiResponse.builder()
                .statusCode(200)
                .message("Success")
                .build();
        return ResponseEntity.ok(body);
    }
}
