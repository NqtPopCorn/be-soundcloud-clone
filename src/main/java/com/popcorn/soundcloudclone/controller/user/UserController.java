package com.popcorn.soundcloudclone.controller.user;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistFilterRequest;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistSummaryResponse;
import com.popcorn.soundcloudclone.domain.dto.user.AdminCreationUserRequest;
import com.popcorn.soundcloudclone.domain.dto.user.AdminUpdateUserRequest;
import com.popcorn.soundcloudclone.domain.dto.user.UserResponse;
import com.popcorn.soundcloudclone.domain.dto.user.UserUpdateRequest;
import com.popcorn.soundcloudclone.domain.service.AlbumService;
import com.popcorn.soundcloudclone.domain.service.PlaylistService;
import com.popcorn.soundcloudclone.domain.service.TrackService;
import com.popcorn.soundcloudclone.domain.service.UserService;
import com.popcorn.soundcloudclone.security.MyUserDetails;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Quan ly thong tin ca nhan nguoi dung
 * GET /user/me
 * PUT /user/me
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsers(
            @RequestParam(required = false) String query,
            @PageableDefault Pageable pageable) {
        var res = userService.getPageUsers(query, pageable);
        return ResponseEntity.ok().body(ApiResponse.<PageResponse<UserResponse>>builder()
                .statusCode(200)
                .message("Success")
                .result(res)
                .build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody AdminCreationUserRequest dto) {
        var res = userService.createAdminRequest(dto);
        return ResponseEntity.ok().body(ApiResponse.<UserResponse>builder()
                .result(res)
                .message("Successfully created user")
                .statusCode(200)
                .build());
    }

    @PatchMapping("/{userId}/authorize")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable int userId,
            @RequestBody AdminUpdateUserRequest request) {
        var res = userService.adminUpdateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .statusCode(200)
                .result(res)
                .message("Update user successfully")
                .build());
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .statusCode(200)
                .message("Deleted user successfully")
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

}
