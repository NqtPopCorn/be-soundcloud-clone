package com.popcorn.soundcloudclone.features.playlist.controller;

import com.popcorn.soundcloudclone.features.playlist.dto.request.PlaylistCreationRequest;
import com.popcorn.soundcloudclone.features.playlist.dto.request.PlaylistFilterRequest;
import com.popcorn.soundcloudclone.features.playlist.dto.request.PlaylistUpdateRequest;
import com.popcorn.soundcloudclone.features.playlist.dto.response.PlaylistResponse;
import com.popcorn.soundcloudclone.features.playlist.dto.response.PlaylistSummaryResponse;
import com.popcorn.soundcloudclone.features.playlist.entity.Playlist;

import com.popcorn.soundcloudclone.common.response.ApiResponse;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.playlist.service.PlaylistService;
import com.popcorn.soundcloudclone.features.user.service.FavoritePlaylistService;
import com.popcorn.soundcloudclone.common.security.MyUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: add permission check, only owner can update/delete playlist
// only auth user can create playlist, anyone can view playlist
@RestController
@RequiredArgsConstructor
@RequestMapping("/playlists")
public class PlaylistController {
        private final PlaylistService playlistService;

        @GetMapping()
        public ResponseEntity<ApiResponse<PageResponse<PlaylistResponse>>> getPage(
                        @PageableDefault Pageable pageable,
                        @ModelAttribute PlaylistFilterRequest dto) {
                var pageResponse = playlistService.findByFilter(dto, pageable);
                var body = ApiResponse.<PageResponse<PlaylistResponse>>builder()
                                .statusCode(200)
                                .message("Success")
                                .result(pageResponse)
                                .build();
                return ResponseEntity.ok(body);
        }

        @GetMapping("/summaries")
        public ResponseEntity<ApiResponse<PageResponse<PlaylistSummaryResponse>>> getPageSummary(
                        @PageableDefault Pageable pageable,
                        @ModelAttribute PlaylistFilterRequest dto) {
                var pageResponse = playlistService.findSummaries(dto, pageable);
                var body = ApiResponse.<PageResponse<PlaylistSummaryResponse>>builder()
                                .statusCode(200)
                                .message("Success")
                                .result(pageResponse)
                                .build();
                return ResponseEntity.ok(body);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<PlaylistResponse>> getById(@PathVariable int id,
                        @AuthenticationPrincipal MyUserDetails userDetails) {
                int userId = userDetails == null ? 0 : userDetails.getUserId();
                var response = playlistService.getById(id, userId);
                var body = ApiResponse.<PlaylistResponse>builder()
                                .statusCode(200)
                                .message("Playlist found")
                                .result(response)
                                .build();
                return ResponseEntity.ok(body);
        }

        @PostMapping
        @PreAuthorize("principal != null && isAuthenticated()")
        public ResponseEntity<ApiResponse> addPlaylist(@AuthenticationPrincipal MyUserDetails principal,
                        @RequestBody @Valid PlaylistCreationRequest request) {
                int userId = principal.getUserId();
                playlistService.create(userId, request);
                return ResponseEntity.ok(ApiResponse.builder()
                                .statusCode(200)
                                .message("Create successfully")
                                .build());
        }

        @PatchMapping(value = "/{id}")
        // @PreAuthorize("principal != null && isAuthenticated()")
        // todo: check owner
        public ResponseEntity<ApiResponse> updatePlaylist(@PathVariable int id,
                        @RequestBody @Valid PlaylistUpdateRequest request) {
                playlistService.patchUpdatePlaylist(id, request);
                return ResponseEntity.ok(ApiResponse.builder()
                                .statusCode(200)
                                .message("Update successfully")
                                .build());
        }

        @PostMapping("/{playlistId}/tracks")
        // todo: check owner
        public ResponseEntity<ApiResponse> addTrackPlaylist(@PathVariable int playlistId,
                        @RequestParam List<Integer> trackIds) {
                playlistService.addTracksToPlaylist(playlistId, trackIds);
                return ResponseEntity.ok(ApiResponse.builder()
                                .statusCode(200)
                                .message("Add track successfully")
                                .build());
        }

        @PutMapping("/{playlistId}/tracks")
        public ResponseEntity<ApiResponse> updatePlaylistTracks(@PathVariable int playlistId,
                        @RequestParam List<Integer> trackIds) {
                playlistService.updatePlaylistTracks(playlistId, trackIds);
                return ResponseEntity.ok(ApiResponse.builder()
                                .statusCode(200)
                                .message("Update playlist tracks successfully")
                                .build());
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse> deletePlaylist(@PathVariable int id) {
                playlistService.deletePlaylist(id);
                return ResponseEntity.ok(ApiResponse.builder()
                                .statusCode(200)
                                .message("Delete playlist successfully")
                                .build());
        }
}
