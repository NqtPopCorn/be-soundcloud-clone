package com.popcorn.soundcloudclone.features.track.controller;

import com.popcorn.soundcloudclone.features.track.dto.request.TrackCreationRequest;
import com.popcorn.soundcloudclone.features.track.dto.request.TrackQueryRequest;
import com.popcorn.soundcloudclone.features.track.dto.request.TrackUpdateRequest;
import com.popcorn.soundcloudclone.features.track.dto.response.TrackResponse;
import com.popcorn.soundcloudclone.features.track.entity.Track;
import com.popcorn.soundcloudclone.features.track.service.TrackService;

import com.popcorn.soundcloudclone.common.response.ApiResponse;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.common.security.MyUserDetails;
import com.popcorn.soundcloudclone.common.security.UserGuard;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/tracks")
public class TrackController {
    private final TrackService trackService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TrackResponse>>> getPage(
            @ModelAttribute TrackQueryRequest request,
            @PageableDefault Pageable pageable) {
        PageResponse<TrackResponse> pageResponse = trackService.getPage(request, pageable);

        return ResponseEntity.ok(ApiResponse.<PageResponse<TrackResponse>>builder()
                .result(pageResponse)
                .message("Success")
                .statusCode(200)
                .build());
    }

    @GetMapping("/{trackId}")
    // @PreAuthorize("@userSecurity.hasTrackPermit(#trackId, #userDetails)")
    public ResponseEntity<ApiResponse<TrackResponse>> getTrackResponse(@PathVariable int trackId,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        Integer userId = userDetails == null ? null : userDetails.getUserId();
        TrackResponse track;
        if (userId == null) {
            track = trackService.getTrackResponse(trackId);
        } else {
            track = trackService.getTrackResponse(trackId);
        }

        var response = ApiResponse.<TrackResponse>builder()
                .result(track)
                .message("Success")
                .statusCode(200)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ARTIST', 'ADMIN')")
    public ResponseEntity<ApiResponse<TrackResponse>> addTrack(@AuthenticationPrincipal MyUserDetails userDetails,
            @ModelAttribute @Valid TrackCreationRequest request) {
        var track = trackService.createTrack(userDetails.getUserId(), request);
        var body = ApiResponse.<TrackResponse>builder()
                .statusCode(200)
                .message("Track created")
                .result(track)
                .build();

        return ResponseEntity.ok(body);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // @PreAuthorize("@userSecurity.hasTrackPermit(#id, #principal)") // => chi
    // admin va owner duoc phep update
    public ResponseEntity<ApiResponse<TrackResponse>> updateTrack(@PathVariable int id,
            @ModelAttribute @Valid TrackUpdateRequest request) {
        var result = trackService.updateTrack(id, request);
        return ResponseEntity.ok(ApiResponse.<TrackResponse>builder()
                .statusCode(200)
                .message("Track updated")
                .result(result)
                .build());
    }

    @DeleteMapping("/{id}")
    // TODO: check permission, only admin and owner can delete
    // @PreAuthorize("@userSecurity.hasTrackPermit(#id, #principal)")
    public ResponseEntity<ApiResponse> deleteTrack(@PathVariable int id) {
        trackService.deleteTrack(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .statusCode(200)
                .message("Track deleted")
                .build());
    }

    @PostMapping("/{id}/play")
    public ResponseEntity<ApiResponse> playTrack(@PathVariable int id,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .statusCode(401)
                            .message("Authentication required to register play")
                            .build());
        }
        trackService.increasePlayCount(id, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.builder()
                .statusCode(200)
                .message("Play registered")
                .build());
    }

}
