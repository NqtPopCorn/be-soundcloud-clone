package com.popcorn.soundcloudclone.controller;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistSummaryResponse;
import com.popcorn.soundcloudclone.domain.dto.track.TrackFilterRequest;
import com.popcorn.soundcloudclone.domain.dto.track.TrackResponse;
import com.popcorn.soundcloudclone.domain.dto.user.UserResponse;
import com.popcorn.soundcloudclone.domain.dto.user.UserUpdateRequest;
import com.popcorn.soundcloudclone.domain.entity.Track;
import com.popcorn.soundcloudclone.security.MyUserDetails;
import com.popcorn.soundcloudclone.service.PlaylistService;
import com.popcorn.soundcloudclone.service.TrackServiceV2;
import com.popcorn.soundcloudclone.service.UserService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * Quan ly thong tin ca nhan nguoi dung
 *     GET     /user/me
 *     PUT     /user/me
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    TrackServiceV2 trackService;
    private final PlaylistService playlistService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal MyUserDetails userDetails) {
        var user = userService.getUserProfileById(userDetails.getUserId());
        var body = ApiResponse.<UserResponse>builder()
                .statusCode(200)
                .message("Get info success")
                .result(user)
                .build();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/me/tracks")
    @PreAuthorize(value = "isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<TrackResponse>>> getPageMyTracks(
            @RequestParam(defaultValue = "") String keyword,
            @Nullable Pageable pageable,
            @AuthenticationPrincipal MyUserDetails user
    ) {
        Integer userId = user.getUserId(); // authenticated then user will never be null
        PageResponse<TrackResponse> pageResponse = trackService.getPageForUser(TrackFilterRequest.builder()
                .keyword(keyword)
                .build(), pageable, userId, null);

        return ResponseEntity.ok(ApiResponse.<PageResponse<TrackResponse>>builder()
                .result(pageResponse)
                .message("Success")
                .statusCode(200)
                .build());
    }

    @GetMapping("/me/playlists")
    @PreAuthorize(value = "isAuthenticated()")
    public ResponseEntity<ApiResponse<List<PlaylistSummaryResponse>>> getAllMyPlaylists(@AuthenticationPrincipal MyUserDetails userDetails) {
        var res = playlistService.getUserPlaylistSummaries(userDetails.getUserId());

        return ResponseEntity.ok(ApiResponse.<List<PlaylistSummaryResponse>>builder()
                        .statusCode(200)
                        .message("Get info success")
                        .result(res)
                .build());
    }


    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo(@PathVariable String username) {
        var user = userService.getUserProfileByUsername(username);
        var body = ApiResponse.<UserResponse>builder()
                .statusCode(200)
                .message("Get info success")
                .result(user)
                .build();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{username}/tracks")
    public ResponseEntity<ApiResponse<PageResponse<TrackResponse>>> getPageUserTracks(
            @PathVariable String username,
            @RequestParam(defaultValue = "") String keyword,
            @Nullable Pageable pageable,
            @AuthenticationPrincipal MyUserDetails user
    ) {
        Integer userId = user == null ? null: user.getUserId();
        PageResponse<TrackResponse> pageResponse = trackService.getPageForUser(TrackFilterRequest.builder()
                .keyword(keyword)
                .artistName(username)
                .build(), pageable, userId, Track.Privacy.PUBLIC);

        return ResponseEntity.ok(ApiResponse.<PageResponse<TrackResponse>>builder()
                .result(pageResponse)
                .message("Success")
                .statusCode(200)
                .build());
    }

    // TODO: create user api - for admin
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser() {
        return null;
    }

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> putMe(@AuthenticationPrincipal MyUserDetails userDetails, @ModelAttribute @Valid UserUpdateRequest request) {
        var res = userService.updateUser(userDetails.getUserId(), request);

        var body = ApiResponse.<UserResponse>builder()
                .statusCode(200)
                .message("Update success")
                .result(res)
                .build();
        return ResponseEntity.status(200)
                .body(body);
    }

    /**
     * Delete if upload null, else update
     */
    @PutMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> updateOrDeleteAvatar(
            @AuthenticationPrincipal MyUserDetails user,
            @RequestPart(value = "avatarUpload", required = false) MultipartFile file) {
        int userId = user.getUserId();

        if (file == null || file.isEmpty()) {
            userService.deleteAvatar(userId);
        } else {
            userService.updateAvatar(userId, file);
        }

        var body = ApiResponse.builder()
                .statusCode(200)
                .message("Update success")
                .build();
        return ResponseEntity.ok(body);
    }

    /**
     * Update if not null , else delete
     */
    @PutMapping(value = "/me/background", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> updateOrDeleteBackground(
            @AuthenticationPrincipal MyUserDetails user,
            @RequestPart(value = "backgroundUpload", required = false) MultipartFile file) {
        int userId = user.getUserId();

        if (file == null || file.isEmpty()) {
            userService.deleteBackgroundImage(userId);
        } else {
            userService.updateBackgroundImage(userId, file);
        }

        var body = ApiResponse.builder()
                .statusCode(200)
                .message("Update success")
                .build();
        return ResponseEntity.ok(body);
    }

    // post: follow user
    @PostMapping("/{artistId}/follow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> followUser(
            @PathVariable int artistId,
            @AuthenticationPrincipal MyUserDetails user) {
        int userId = user.getUserId();
        userService.followUser(userId, artistId);
        return ResponseEntity.ok(ApiResponse.builder()
                        .statusCode(200)
                        .message("Follow success")
                .build());
    }

    @DeleteMapping("/{artistId}/follow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> unFollowUser(@PathVariable int artistId, @AuthenticationPrincipal MyUserDetails user) {
        int userId = user.getUserId();
        userService.unFollowUser(userId, artistId);
        return ResponseEntity.ok(ApiResponse.builder()
                .statusCode(200)
                .message("Unfollow success")
                .build());
    }
}
