package com.popcorn.soundcloudclone.controller.user;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.user.UserResponse;
import com.popcorn.soundcloudclone.domain.dto.user.UserSummaryResponse;
import com.popcorn.soundcloudclone.domain.service.FavoriteArtistService;
import com.popcorn.soundcloudclone.security.MyUserDetails;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/me/favorite-artists")
@RequiredArgsConstructor
public class FavoriteAritstController {

    private final FavoriteArtistService favoriteArtistService;

    @GetMapping
    @PreAuthorize("isAuthenticated() && #userDetails != null")
    public ApiResponse<PageResponse<UserSummaryResponse>> getFavoriteArtists(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @PageableDefault Pageable pageable) {
        var res = favoriteArtistService.getFavoriteArtists(userDetails.getUserId(), pageable);
        return ApiResponse.<PageResponse<UserSummaryResponse>>builder()
                .result(res)
                .statusCode(200)
                .message("Success")
                .build();
    }

    @PostMapping("/{artistId}")
    @PreAuthorize("isAuthenticated() && #userDetails != null")
    public ApiResponse followArtist(@AuthenticationPrincipal MyUserDetails userDetails,
            @PathVariable Integer artistId) {
        favoriteArtistService.followArtist(artistId, userDetails.getUserId());
        return ApiResponse.builder()
                .statusCode(200)
                .message("Success")
                .build();
    }

    @DeleteMapping("/{artistId}")
    @PreAuthorize("isAuthenticated() && #userDetails != null")
    public ApiResponse unFollowArtist(@AuthenticationPrincipal MyUserDetails userDetails,
            @PathVariable Integer artistId) {
        favoriteArtistService.unfollowArtist(artistId, userDetails.getUserId());
        return ApiResponse.builder()
                .statusCode(200)
                .message("Success")
                .build();
    }
}
