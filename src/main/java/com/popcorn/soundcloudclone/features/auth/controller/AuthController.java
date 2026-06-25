package com.popcorn.soundcloudclone.features.auth.controller;

import com.popcorn.soundcloudclone.common.response.ApiResponse;
import com.popcorn.soundcloudclone.features.auth.dto.response.AuthResponse;
import com.popcorn.soundcloudclone.features.auth.service.AuthService;
import com.popcorn.soundcloudclone.features.user.service.UserService;
import com.popcorn.soundcloudclone.features.users.dto.request.UserCreationRequest;
import com.popcorn.soundcloudclone.features.users.dto.response.UserResponse;
import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.auth.dto.request.LoginBody;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import com.popcorn.soundcloudclone.common.security.MyUserDetails;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
        private final AuthService authService;

        // dang nhap
        @PostMapping("/login")
        public ResponseEntity<ApiResponse<AuthResponse>> authenticate(@RequestBody LoginBody loginBody) {
                var authResponse = authService.authenticate(loginBody.getUsername(), loginBody.getPassword());
                var body = ApiResponse.<AuthResponse>builder()
                                .statusCode(200)
                                .message("Success")
                                .result(authResponse)
                                .build();

                String refreshToken = authResponse.getRefreshToken();
                long refreshTokenExpiresIn = authResponse.getRefreshTokenExpiresIn();

                ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                                .httpOnly(true)
                                .secure(false)
                                .path("/")
                                .maxAge(refreshTokenExpiresIn)
                                .sameSite("Lax")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                                .body(body);
        }

        @PostMapping("/google")
        public ResponseEntity<ApiResponse<AuthResponse>> authenticateWithGoogle(@RequestBody com.popcorn.soundcloudclone.features.auth.dto.request.GoogleLoginBody loginBody) {
                var authResponse = authService.authenticateWithGoogle(loginBody.getCredential());
                var body = ApiResponse.<AuthResponse>builder()
                                .statusCode(200)
                                .message("Success")
                                .result(authResponse)
                                .build();

                String refreshToken = authResponse.getRefreshToken();
                long refreshTokenExpiresIn = authResponse.getRefreshTokenExpiresIn();

                ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                                .httpOnly(true)
                                .secure(false)
                                .path("/")
                                .maxAge(refreshTokenExpiresIn)
                                .sameSite("Lax")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                                .body(body);
        }

        @PostMapping("/logout")
        public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal MyUserDetails userDetails,
                        HttpServletRequest request) {
                String refreshToken = readCookie(request, "refresh_token");
                if (userDetails == null || refreshToken == null) {
                        return ResponseEntity.badRequest().build();
                }

                authService.logout(refreshToken);

                var response = ApiResponse.<Void>builder()
                                .result(null)
                                .message("Logout successful")
                                .build();
                // clear cookie
                ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                                .httpOnly(true)
                                .secure(false)
                                .path("/")
                                .maxAge(0)
                                .sameSite("Lax")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(response);
        }

        @PostMapping("/register")
        public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody UserCreationRequest request) {
                var response = authService.register(request);
                return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                                .statusCode(200)
                                .message("User registered")
                                .result(response)
                                .build());
        }

        @PostMapping("/refresh")
        public ResponseEntity<ApiResponse<String>> refresh(HttpServletRequest request) {
                String refreshToken = readCookie(request, "refresh_token");
                if (refreshToken == null) {
                        return ResponseEntity.badRequest().build();
                }

                String accessToken = authService.refreshToken(refreshToken);

                var response = ApiResponse.<String>builder()
                                .message("Refresh token successful")
                                .result(accessToken)
                                .build();

                return ResponseEntity.ok().body(response);
        }

        private String readCookie(HttpServletRequest request, String name) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                        return Arrays.stream(cookies)
                                        .filter(c -> c.getName().equals(name))
                                        .findFirst()
                                        .map(Cookie::getValue)
                                        .orElse(null);
                }
                return null;
        }
}
