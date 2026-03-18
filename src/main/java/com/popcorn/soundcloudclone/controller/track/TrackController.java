package com.popcorn.soundcloudclone.controller.track;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.track.*;
import com.popcorn.soundcloudclone.domain.service.*;
import com.popcorn.soundcloudclone.security.MyUserDetails;
import com.popcorn.soundcloudclone.security.UserSecurity;

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
        Integer userId = userDetails.getUserId();
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
    // @PreAuthorize("hasAnyRole('ADMIN', 'ARTIST')")
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

}
