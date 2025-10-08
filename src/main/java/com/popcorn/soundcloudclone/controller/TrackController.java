package com.popcorn.soundcloudclone.controller;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.track.*;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.entity.Track;
import com.popcorn.soundcloudclone.security.MyUserDetails;
import com.popcorn.soundcloudclone.service.GenreService;
import com.popcorn.soundcloudclone.service.PlayCache;
import com.popcorn.soundcloudclone.service.TrackService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/track")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TrackController {
    private final TrackService trackService;
    private final GenreService genreService;
    private final PlayCache playCache;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TrackResponse>>> getPage(
        @RequestParam(defaultValue = "") String keyword,
        @Nullable Pageable pageable,
        @AuthenticationPrincipal MyUserDetails userDetail
    ) {
        PageResponse<TrackResponse> pageResponse =
                trackService.getPage(keyword, null, Track.Privacy.PUBLIC, pageable);

        return ResponseEntity.ok(ApiResponse.<PageResponse<TrackResponse>>builder()
                .result(pageResponse)
                .message("Success")
                .code(1000)
                .build()
        );
    }


    @GetMapping("/{trackId}")
    public ResponseEntity<ApiResponse<TrackResponse>> getFullTrackResponse(@PathVariable int trackId, @AuthenticationPrincipal MyUserDetails userDetails) {
        var response = ApiResponse.<TrackResponse>builder()
                .result(trackService.getTrackResponse(trackId))
                .message("Success")
                .code(1000)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{trackId}/stream")
    public ResponseEntity<ResourceRegion> streamAudio(
            @PathVariable Integer trackId,
            @RequestHeader HttpHeaders headers,
            Authentication auth) throws IOException {

        Path path = Paths.get(trackService.getAudioFilePath(trackId));
        FileSystemResource audio = new FileSystemResource(path);
        long contentLength = audio.contentLength();

        // Tìm byte range client yêu cầu
        HttpRange range = headers.getRange().isEmpty()
                ? HttpRange.createByteRange(0, contentLength - 1)
                : headers.getRange().get(0);

        long start = range.getRangeStart(contentLength);
        long end   = range.getRangeEnd(contentLength);
        long rangeLength = Math.min(1_000_000, end - start + 1); // 1 MB chunk

        ResourceRegion region = new ResourceRegion(audio, start, rangeLength);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(audio).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ArtistTrackResponse>> addTrack(Authentication authentication, @ModelAttribute @Valid TrackCreationRequest request) {
        int userId = Integer.parseInt(authentication.getName());
        var track = trackService.createTrack(userId, request);
        var body = ApiResponse.<ArtistTrackResponse>builder()
                .code(1000)
                .message("Track created")
                .result(track)
                .build();

        return ResponseEntity.ok(body);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@userSecurity.isTrackOwnerOrAdmin(#id, authentication)") // => chi admin va owner duoc phep update
    public ResponseEntity<ApiResponse<Void>> updateTrack(@PathVariable int id, @ModelAttribute @Valid TrackUpdateRequest request) {
        trackService.updateTrack(id, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(1000)
                .message("Track updated")
                .build());
    }

    /**
     * increase play count
     */
    @PostMapping("/{trackId}/play")
    public ResponseEntity<ApiResponse> playTrack(@PathVariable int trackId, Authentication auth) {
        // neu nguoi dung da dang nhap thi increase play count
        if(auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        MyUserDetails userDetails = (MyUserDetails)auth.getPrincipal();
        trackService.increasePlayCount(trackId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.builder()
                        .code(1000)
                        .message("Play counted successfully!")
                .build());
    }

    @PostMapping("/{trackId}/like")
    public ResponseEntity<ApiResponse> likeTrack(@PathVariable int trackId, Authentication authentication) {
        int userId = Integer.parseInt(authentication.getName());
        trackService.likeTrack(trackId, userId);
        return ResponseEntity.ok(ApiResponse.builder()
                        .code(1000)
                        .message("Liked track")
                .build());
    }

    @DeleteMapping("/{trackId}/like")
    public ResponseEntity<ApiResponse> unlikeTrack(@PathVariable int trackId, Authentication authentication) {
        int userId = Integer.parseInt(authentication.getName());
        trackService.unLikeTrack(trackId, userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .code(1000)
                .message("Unliked track")
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@userSecurity.isTrackOwnerOrAdmin(#id, authentication)")
    public ResponseEntity<ApiResponse> deleteTrack(@PathVariable int id) {
        trackService.deleteTrack(id);
        return ResponseEntity.ok(ApiResponse.builder()
                    .code(1000)
                    .message("Track deleted")
                .build());
    }

    @PostMapping("/genre")
    // yeu cau role admin?
    public ResponseEntity<ApiResponse<GenreResponse>> addGenre(@RequestParam String genreName) {
        var genre = genreService.create(genreName);
        return ResponseEntity.ok(ApiResponse.<GenreResponse>builder()
                        .code(1000)
                        .message("Add genre successfully!")
                        .result(genre)
                .build());
    }

    @GetMapping("/genre")
    public ResponseEntity<ApiResponse<List<GenreResponse>>> getGenres() {
        var genre = genreService.findAll();
        return ResponseEntity.ok(ApiResponse.<List<GenreResponse>>builder()
                .code(1000)
                .message("Success")
                .result(genre)
                .build());
    }

    @DeleteMapping("/genre/:genreName")
    public ResponseEntity<ApiResponse> deleteGenre(@RequestParam String genreName) {
        genreService.delete(genreName);
        return ResponseEntity.ok(ApiResponse.builder()
                .code(1000)
                .message("Add tag successfully!")
                .build());
    }
}
