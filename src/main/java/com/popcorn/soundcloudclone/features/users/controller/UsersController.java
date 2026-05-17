package com.popcorn.soundcloudclone.features.users.controller;

import com.popcorn.soundcloudclone.common.response.ApiResponse;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.users.dto.request.AdminCreationUserRequest;
import com.popcorn.soundcloudclone.features.users.dto.request.AdminUpdateUserRequest;
import com.popcorn.soundcloudclone.features.users.dto.response.UserResponse;
import com.popcorn.soundcloudclone.features.users.service.AdminUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Quan ly nguoi dung
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final AdminUserService adminUserService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsers(
            @RequestParam(required = false) String query,
            @PageableDefault Pageable pageable) {
        var res = adminUserService.getPageUsers(query, pageable);
        return ResponseEntity.ok().body(ApiResponse.<PageResponse<UserResponse>>builder()
                .statusCode(200)
                .message("Success")
                .result(res)
                .build());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo(@PathVariable int userId) {
        var user = adminUserService.getUserById(userId);
        var body = ApiResponse.<UserResponse>builder()
                .statusCode(200)
                .message("Get info success")
                .result(user)
                .build();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/byname")
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo(@RequestParam String username) {
        var user = adminUserService.getUserByUsername(username);
        var body = ApiResponse.<UserResponse>builder()
                .statusCode(200)
                .message("Get info success")
                .result(user)
                .build();
        return ResponseEntity.ok(body);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody @Valid AdminCreationUserRequest dto) {
        var res = adminUserService.createUser(dto);
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
        var res = adminUserService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .statusCode(200)
                .result(res)
                .message("Update user successfully")
                .build());
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable int userId) {
        adminUserService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .statusCode(200)
                .message("Deleted user successfully")
                .build());
    }

}
