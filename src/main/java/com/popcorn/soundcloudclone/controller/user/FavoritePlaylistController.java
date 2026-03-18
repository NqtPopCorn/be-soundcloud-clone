package com.popcorn.soundcloudclone.controller.user;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistFilterRequest;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistResponse;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistSummaryResponse;
import com.popcorn.soundcloudclone.domain.service.FavoritePlaylistService;
import com.popcorn.soundcloudclone.domain.service.PlaylistService;
import com.popcorn.soundcloudclone.security.MyUserDetails;

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
@RequestMapping("/users/me/favorite-playlists")
@RequiredArgsConstructor
public class FavoritePlaylistController {
    private final FavoritePlaylistService favoritePlaylistService;
    private final PlaylistService playlistService;

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

    @PostMapping("/{playlistId}/like")
    @PreAuthorize("isAuthenticated() && #userDetails != null")
    public ResponseEntity<ApiResponse> likePlaylist(
            @PathVariable Integer playlistId,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        favoritePlaylistService.likePlaylist(userDetails.getUserId(), playlistId);
        var body = ApiResponse.builder()
                .statusCode(200)
                .message("Success")
                .build();
        return ResponseEntity.ok(body);
    }

    @PostMapping("/{playlistId}/unlike")
    @PreAuthorize("isAuthenticated() && #userDetails != null")
    public ResponseEntity<ApiResponse> unlikePlaylist(
            @PathVariable Integer playlistId,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        favoritePlaylistService.unlikePlaylist(userDetails.getUserId(), playlistId);
        var body = ApiResponse.<List<PlaylistResponse>>builder()
                .statusCode(200)
                .message("Success")
                .build();
        return ResponseEntity.ok(body);
    }
}
