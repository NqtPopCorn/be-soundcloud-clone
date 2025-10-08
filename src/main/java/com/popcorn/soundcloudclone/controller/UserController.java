package com.popcorn.soundcloudclone.controller;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.track.ArtistTrackResponse;
import com.popcorn.soundcloudclone.domain.dto.track.TrackResponse;
import com.popcorn.soundcloudclone.domain.dto.user.UserResponse;
import com.popcorn.soundcloudclone.domain.dto.user.UserUpdateRequest;
import com.popcorn.soundcloudclone.domain.entity.Track;
import com.popcorn.soundcloudclone.security.MyUserDetails;
import com.popcorn.soundcloudclone.service.TrackService;
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
    TrackService trackService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(Authentication auth) {
        int userId = Integer.parseInt(auth.getName());
        var user = userService.userGetInfo(userId);
        var body = ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Get info success")
                .result(user)
                .build();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/me/tracks")
    @PreAuthorize(value = "authentication.principal != null")
    public ResponseEntity<ApiResponse<PageResponse<ArtistTrackResponse>>> getPageMyTracks(
            @RequestParam(defaultValue = "") String keyword,
            @Nullable Pageable pageable,
            Authentication auth
    ) {
        String username = ((UserDetails)auth.getPrincipal()).getUsername();
        PageResponse<ArtistTrackResponse> pageResponse = trackService.getArtistTrackPage(keyword, username, Track.Privacy.PUBLIC, pageable);

        return ResponseEntity.ok(ApiResponse.<PageResponse<ArtistTrackResponse>>builder()
                .result(pageResponse)
                .message("Success")
                .code(1000)
                .build());
    }


    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo(@PathVariable String username) {
        var user = userService.userGetInfoByUsername(username);
        var body = ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Get info success")
                .result(user)
                .build();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{username}/tracks")
    public ResponseEntity<ApiResponse<PageResponse<TrackResponse>>> getPageUserTracks(
            @PathVariable String username,
            @RequestParam(defaultValue = "") String keyword,
            @Nullable Pageable pageable
    ) {
        PageResponse<TrackResponse> pageResponse = trackService.getPage(keyword, username, Track.Privacy.PUBLIC, pageable);

        return ResponseEntity.ok(ApiResponse.<PageResponse<TrackResponse>>builder()
                .result(pageResponse)
                .message("Success")
                .code(1000)
                .build());
    }

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> putMe(Authentication auth, @ModelAttribute @Valid UserUpdateRequest request) {
        int userId = Integer.parseInt(auth.getName());
        var res = userService.updateUser(userId, request);

        var body = ApiResponse.<UserResponse>builder()
                .code(1000)
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
                .code(1000)
                .message("Update success")
                .build();
        return ResponseEntity.ok(body);
    }

    /**
     * Update if not null , else delete
     */
    @PutMapping(value = "/me/background", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
                .code(1000)
                .message("Update success")
                .build();
        return ResponseEntity.ok(body);
    }

    // post: follow user
    @PostMapping("/{artistId}/follow")
    public ResponseEntity<ApiResponse> followUser(
            @PathVariable int artistId,
            @AuthenticationPrincipal MyUserDetails user) {
        int userId = user.getUserId();
        userService.followUser(userId, artistId);
        return ResponseEntity.ok(ApiResponse.builder()
                        .code(1000)
                        .message("Follow success")
                .build());
    }

    @DeleteMapping("/{artistId}/follow")
    public ResponseEntity<ApiResponse> unFollowUser(@PathVariable int artistId, @AuthenticationPrincipal MyUserDetails user) {
        int userId = user.getUserId();
        userService.unFollowUser(userId, artistId);
        return ResponseEntity.ok(ApiResponse.builder()
                .code(1000)
                .message("Unfollow success")
                .build());
    }
}
