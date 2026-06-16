package com.popcorn.soundcloudclone.features.upgradeartist.controller;

import com.popcorn.soundcloudclone.common.response.ApiResponse;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.common.security.MyUserDetails;
import com.popcorn.soundcloudclone.features.upgradeartist.dto.ArtistUpgradeRequestResponse;
import com.popcorn.soundcloudclone.features.upgradeartist.dto.RejectArtistUpgradeRequest;
import com.popcorn.soundcloudclone.features.upgradeartist.entity.ArtistUpgradeRequest;
import com.popcorn.soundcloudclone.features.upgradeartist.service.ArtistUpgradeRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArtistUpgradeRequestController {
    private final ArtistUpgradeRequestService service;

    @PostMapping("/user/me/artist-upgrade-request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ArtistUpgradeRequestResponse>> createRequest(
            @AuthenticationPrincipal MyUserDetails userDetails) {
        var result = service.createRequest(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.<ArtistUpgradeRequestResponse>builder()
                .statusCode(200)
                .message("Artist upgrade request created")
                .result(result)
                .build());
    }

    @GetMapping("/user/me/artist-upgrade-request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ArtistUpgradeRequestResponse>> getLatestRequest(
            @AuthenticationPrincipal MyUserDetails userDetails) {
        var result = service.getLatestRequest(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.<ArtistUpgradeRequestResponse>builder()
                .statusCode(200)
                .message("Success")
                .result(result)
                .build());
    }

    @GetMapping("/users/artist-upgrade-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ArtistUpgradeRequestResponse>>> getRequests(
            @RequestParam(required = false) ArtistUpgradeRequest.Status status,
            @PageableDefault Pageable pageable) {
        var result = service.getRequests(status, pageable);
        return ResponseEntity.ok(ApiResponse.<PageResponse<ArtistUpgradeRequestResponse>>builder()
                .statusCode(200)
                .message("Success")
                .result(result)
                .build());
    }

    @PatchMapping("/users/artist-upgrade-requests/{requestId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ArtistUpgradeRequestResponse>> approve(
            @PathVariable int requestId,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        var result = service.approve(requestId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.<ArtistUpgradeRequestResponse>builder()
                .statusCode(200)
                .message("Artist upgrade request approved")
                .result(result)
                .build());
    }

    @PatchMapping("/users/artist-upgrade-requests/{requestId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ArtistUpgradeRequestResponse>> reject(
            @PathVariable int requestId,
            @AuthenticationPrincipal MyUserDetails userDetails,
            @RequestBody(required = false) RejectArtistUpgradeRequest request) {
        String note = request == null ? null : request.getNote();
        var result = service.reject(requestId, userDetails.getUserId(), note);
        return ResponseEntity.ok(ApiResponse.<ArtistUpgradeRequestResponse>builder()
                .statusCode(200)
                .message("Artist upgrade request rejected")
                .result(result)
                .build());
    }
}
