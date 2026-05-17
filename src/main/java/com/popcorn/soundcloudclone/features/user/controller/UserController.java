package com.popcorn.soundcloudclone.features.user.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.popcorn.soundcloudclone.common.response.ApiResponse;
import com.popcorn.soundcloudclone.common.security.MyUserDetails;
import com.popcorn.soundcloudclone.features.user.service.ProfileMediaService;
import com.popcorn.soundcloudclone.features.user.service.UserService;
import com.popcorn.soundcloudclone.features.users.dto.request.UserUpdateRequest;
import com.popcorn.soundcloudclone.features.users.dto.response.UserResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProfileMediaService profileMediaService;

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> getMe(@AuthenticationPrincipal MyUserDetails user) {
        var res = userService.getUserProfileById(user.getUserId());
        return ResponseEntity.ok(ApiResponse.<Object>builder()
                .statusCode(200)
                .message("Get me success")
                .result(res)
                .build());
    }

    /**
     * Delete if upload null, else update
     */
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> updateOrDeleteAvatar(
            @AuthenticationPrincipal MyUserDetails user,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        if (file == null || file.isEmpty()) {
            profileMediaService.deleteAvatar(user.getUserId());
        } else {
            profileMediaService.updateAvatar(user.getUserId(), file);
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
    @PostMapping(value = "/background", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> updateOrDeleteBackground(
            @AuthenticationPrincipal MyUserDetails user,
            @RequestPart(value = "backgroundUpload", required = false) MultipartFile file) {

        if (file == null || file.isEmpty()) {
            profileMediaService.deleteBackgroundImage(user.getUserId());
        } else {
            profileMediaService.updateBackgroundImage(user.getUserId(), file);
        }

        var body = ApiResponse.builder()
                .statusCode(200)
                .message("Update success")
                .build();
        return ResponseEntity.ok(body);
    }

    @PatchMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal MyUserDetails userDetails,
            @ModelAttribute @Valid UserUpdateRequest request) {
        var res = userService.patchUpdateUser(userDetails.getUserId(), request);

        var body = ApiResponse.<UserResponse>builder()
                .statusCode(200)
                .message("Update success")
                .result(res)
                .build();
        return ResponseEntity.status(200)
                .body(body);
    }

}
