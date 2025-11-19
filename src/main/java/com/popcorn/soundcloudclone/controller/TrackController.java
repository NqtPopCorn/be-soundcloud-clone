package com.popcorn.soundcloudclone.controller;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.track.*;
import com.popcorn.soundcloudclone.domain.entity.Track;
import com.popcorn.soundcloudclone.security.MyUserDetails;
import com.popcorn.soundcloudclone.security.UserSecurity;
import com.popcorn.soundcloudclone.service.*;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/track")
public class TrackController {
    private final TrackServiceV2 trackService;
    private final GenreService genreService;
    private final PlayCache playCache;
    private final UserSecurity userSecurity;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TrackResponse>>> getPage(
        @Nullable TrackFilterRequest request,
        @Nullable Pageable pageable,
        @AuthenticationPrincipal MyUserDetails user
    ) {
        Integer userId = user == null ? null: user.getUserId();
        PageResponse<TrackResponse> pageResponse =
                trackService.getPageForUser(request, pageable, userId, Track.Privacy.PUBLIC);

        return ResponseEntity.ok(ApiResponse.<PageResponse<TrackResponse>>builder()
                .result(pageResponse)
                .message("Success")
                .statusCode(200)
                .build()
        );
    }


    @GetMapping("/{trackId}")
    public ResponseEntity<ApiResponse<TrackResponse>> getTrackResponse(@PathVariable int trackId, @AuthenticationPrincipal MyUserDetails userDetails) {
        var track = trackService.getTrackResponse(trackId);
        boolean isPermitted = userSecurity.hasTrackPermit(trackId, userDetails);
        boolean isPublic = Objects.equals(track.getPrivacy(), Track.Privacy.PUBLIC.name());

        if(!isPermitted|| !isPublic) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.<TrackResponse>builder()
                    .statusCode(403)
                    .message("This track is private")
                    .build());
        }

        var response = ApiResponse.<TrackResponse>builder()
                .result(track)
                .message("Success")
                .statusCode(200)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'ARTIST')")
    public ResponseEntity<ApiResponse<TrackResponse>> addTrack(@AuthenticationPrincipal MyUserDetails userDetails, @ModelAttribute @Valid TrackCreationRequest request) {
        var track = trackService.createTrack(userDetails.getUserId(), request);
        var body = ApiResponse.<TrackResponse>builder()
                .statusCode(200)
                .message("Track created")
                .result(track)
                .build();

        return ResponseEntity.ok(body);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@userSecurity.hasTrackPermit(#id, principal)") // => chi admin va owner duoc phep update
    public ResponseEntity<ApiResponse<TrackResponse>> updateTrack(@PathVariable int id, @ModelAttribute @Valid TrackUpdateRequest request) {
        var result = trackService.updateTrack(id, request);
        return ResponseEntity.ok(ApiResponse.<TrackResponse>builder()
                .statusCode(200)
                .message("Track updated")
                .result(result)
                .build());
    }

    /**
     * increase play count when FE timeout and trigger
     */
    @PostMapping("/{trackId}/play")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> playTrack(@PathVariable int trackId, Authentication auth) {
        MyUserDetails userDetails = (MyUserDetails)auth.getPrincipal();
        trackService.increasePlayCount(trackId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.builder()
                        .statusCode(200)
                        .message("Play counted successfully!")
                .build());
    }

    @PostMapping("/{trackId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> likeTrack(@PathVariable int trackId, @AuthenticationPrincipal MyUserDetails userDetails) {
        trackService.likeTrack(trackId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.builder()
                        .statusCode(200)
                        .message("Liked track")
                .build());
    }

    @DeleteMapping("/{trackId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> unlikeTrack(@PathVariable int trackId, @AuthenticationPrincipal MyUserDetails userDetails) {
        trackService.unLikeTrack(trackId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.builder()
                .statusCode(200)
                .message("Unliked track")
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@userSecurity.hasTrackPermit(#id, principal)")
    public ResponseEntity<ApiResponse> deleteTrack(@PathVariable int id) {
        trackService.deleteTrack(id);
        return ResponseEntity.ok(ApiResponse.builder()
                    .statusCode(200)
                    .message("Track deleted")
                .build());
    }

    @PostMapping("/genre")
    // yeu cau role admin?
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GenreResponse>> addGenre(@RequestParam String genreName) {
        var genre = genreService.create(genreName);
        return ResponseEntity.ok(ApiResponse.<GenreResponse>builder()
                        .statusCode(200)
                        .message("Add genre successfully!")
                        .result(genre)
                .build());
    }

    @GetMapping("/genre")
    public ResponseEntity<ApiResponse<List<GenreResponse>>> getGenres() {
        var genre = genreService.findAll();
        return ResponseEntity.ok(ApiResponse.<List<GenreResponse>>builder()
                .statusCode(200)
                .message("Success")
                .result(genre)
                .build());
    }

    @DeleteMapping("/genre/:genreName")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteGenre(@RequestParam String genreName) {
        genreService.delete(genreName);
        return ResponseEntity.ok(ApiResponse.builder()
                .statusCode(200)
                .message("Add tag successfully!")
                .build());
    }
}
