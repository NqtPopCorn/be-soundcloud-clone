package com.popcorn.soundcloudclone.controller;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.user.AdminCreationUserRequest;
import com.popcorn.soundcloudclone.domain.dto.user.AdminUpdateUserRequest;
import com.popcorn.soundcloudclone.domain.dto.user.UserResponse;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Quan ly user, phan quyen
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/admin")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {
    UserService userService;

    @PostMapping("/users")
    ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody @Valid AdminCreationUserRequest request) {
        var body = ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Success")
                .result(userService.createAdminRequest(request))
                .build();

        return ResponseEntity.ok(body);
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sortType
    ) {
        var pageResponse = userService.getPageUsers(keyword, page, size, sortType);
        var body = ApiResponse.<PageResponse<UserResponse>>builder()
                .code(1000)
                .message("Success")
                .result(pageResponse)
                .build();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/users/{id}")
    ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable int id) {
        var body = ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Success")
                .result(userService.userGetInfo(id))
                .build();
        return ResponseEntity.ok(body);
    }

    @PutMapping(value = "/users/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable int id, @ModelAttribute @Valid AdminUpdateUserRequest request) {
        var body = ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Update success")
                .result(userService.adminUpdateUser(id, request))
                .build();

        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("#id != authentication.name") // khong the xoa tai khoan dang dang nhap
    ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        var body = ApiResponse.<Void>builder()
                .code(1000)
                .message("Delete success")
                .build();

        return ResponseEntity.ok(body);
    }

    /**
     * Delete if upload null, else update
     */
    @PutMapping(value = "/users/{userId}/background", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> updateOrDeleteBackground(@PathVariable int userId, @RequestPart(value = "file", required = false) MultipartFile file) {

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

    /**
     * Delete if upload null, else update
     */
    @PutMapping(value = "/users/{userId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> updateOrDeleteAvatar(@PathVariable int userId, @RequestPart(value = "file", required = false) MultipartFile file) {

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

}
