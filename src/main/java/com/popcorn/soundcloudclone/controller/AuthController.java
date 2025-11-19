package com.popcorn.soundcloudclone.controller;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.auth.AuthResponse;
import com.popcorn.soundcloudclone.domain.dto.auth.LoginBody;
import com.popcorn.soundcloudclone.domain.dto.user.UserCreationRequest;
import com.popcorn.soundcloudclone.domain.dto.user.UserResponse;
import com.popcorn.soundcloudclone.service.AuthService;
import com.popcorn.soundcloudclone.service.UserService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;
    private final UserService userService;

    // dang nhap
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticate(@RequestBody LoginBody loginBody) {
        var authResponse = authService.authenticate(loginBody.getUsername(), loginBody.getPassword());
        var body = ApiResponse.<AuthResponse>builder()
                .result(authResponse)
                .build();

        String token = authResponse.getToken();

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .body(body);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {

        var response = ApiResponse.<Void>builder()
                .result(null)
                .message("Logout successful")
                .build();

        return ResponseEntity.ok()
                .body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody UserCreationRequest request) {
        var response = userService.createRequest(request);
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                    .statusCode(200)
                    .message("User registered")
                    .result(response)
                .build());
    }

    @GetMapping("/introspect")
    @PreAuthorize("authentication != null")
    ResponseEntity<ApiResponse<AuthResponse>> introspect(@AuthenticationPrincipal UserDetails userDetails) {
        var authResponse = AuthResponse.builder()
                .user(userService.getUserProfileByUsername(userDetails.getUsername()))
                .build();

        var body = ApiResponse.<AuthResponse>builder()
                .statusCode(200)
                .message("Success")
                .result(authResponse)
                .build();
        return ResponseEntity.ok(body);
    }

    // refresh
//    @PostMapping("/refresh")
//    @PreAuthorize("authentication != null")
//    ResponseEntity<ApiResponse<AuthResponse>> refresh(@AuthenticationPrincipal UserDetails userDetails) {
//
//        return null;
//    }
}
