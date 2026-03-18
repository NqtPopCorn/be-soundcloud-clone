package com.popcorn.soundcloudclone.controller.user;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.track.TrackResponse;
import com.popcorn.soundcloudclone.domain.service.FavoriteTrackService;
import com.popcorn.soundcloudclone.domain.service.TrackService;
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
@RequestMapping("/users/me/favorite-tracks")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class FavoriteTrackController {
    private final FavoriteTrackService favoriteTrackService;
    private final TrackService trackService;
    // GET /favorite/recent-play

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

    @PostMapping("/like/{trackId}")
    @PreAuthorize("isAuthenticated() && #userDetails != null")
    public ResponseEntity<ApiResponse> likeTrack(
            @PathVariable Integer trackId,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        favoriteTrackService.likeTrack(userDetails.getUserId(), trackId);

        var body = ApiResponse.builder()
                .statusCode(200)
                .message("Success")
                .build();
        return ResponseEntity.ok(body);
    }

    @PostMapping("/unlike/{trackId}")
    @PreAuthorize("isAuthenticated() && #userDetails != null")
    public ResponseEntity<ApiResponse> unlikeTrack(
            @PathVariable Integer trackId,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        favoriteTrackService.unlikeTrack(userDetails.getUserId(), trackId);

        var body = ApiResponse.builder()
                .statusCode(200)
                .message("Success")
                .build();
        return ResponseEntity.ok(body);
    }

    // **** INTERACTION ****
    // @PostMapping("/playCount/{trackId}")
    // @PreAuthorize("isAuthenticated()")
    // public ResponseEntity<ApiResponse> playTrack(@PathVariable int trackId,
    // @AuthenticationPrincipal MyUserDetails userDetails) {
    // trackService.increasePlayCount(trackId, userDetails.getUserId());
    // return ResponseEntity.ok(ApiResponse.builder()
    // .statusCode(200)
    // .message("Play counted successfully!")
    // .build());
    // }

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
