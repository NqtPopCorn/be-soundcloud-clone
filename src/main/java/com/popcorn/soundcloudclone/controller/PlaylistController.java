package com.popcorn.soundcloudclone.controller;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistCreationRequest;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistResponse;
import com.popcorn.soundcloudclone.domain.dto.playlist.PlaylistUpdateRequest;
import com.popcorn.soundcloudclone.security.MyUserDetails;
import com.popcorn.soundcloudclone.service.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/playlist")
public class PlaylistController {
    private final PlaylistService playlistService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PlaylistResponse>>> getPage(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sortType) {
        boolean asc = "asc".equalsIgnoreCase(sortType);
        var pageResponse = playlistService.findByKeyword(keyword, page, size, asc);
        var body = ApiResponse.<PageResponse<PlaylistResponse>>builder()
                .code(1000)
                .message("Success")
                .result(pageResponse)
                .build();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlaylistResponse>> getById(@PathVariable int id) {
        var response = playlistService.getById(id);
        var body = ApiResponse.<PlaylistResponse>builder()
                .code(1000)
                .message("Playlist found")
                .result(response)
                .build();
        return ResponseEntity.ok(body);
    }

    @PostMapping
    @PreAuthorize("authentication != null")
    public ResponseEntity<ApiResponse> addPlaylist(Authentication auth, @RequestBody @Valid PlaylistCreationRequest request) {
        Assert.notNull(auth.getPrincipal(), "principal must not be null");
        int userId = ((MyUserDetails)auth.getPrincipal()).getUserId();
        playlistService.create(userId, request);
        return ResponseEntity.ok(ApiResponse.builder()
                .code(1000)
                .message("Create successfully")
                .build());
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ApiResponse> updatePlaylist(@PathVariable int id, @RequestBody @Valid PlaylistUpdateRequest request) {
        playlistService.updatePlaylist(id, request);
        return ResponseEntity.ok(ApiResponse.builder()
                .code(1000)
                .message("Update successfully")
                .build());
    }

    @PutMapping("/{playlistId}/add-track")
    public ResponseEntity<ApiResponse> addTrackPlaylist(@PathVariable int playlistId, @RequestParam List<Integer> trackIds) {
        playlistService.addTracksToPlaylist(playlistId, trackIds);
        return ResponseEntity.ok(ApiResponse.builder()
                .code(1000)
                .message("Add track successfully")
                .build());
    }

    @PutMapping("/{playlistId}/tracks")
    public ResponseEntity<ApiResponse> updatePlaylistTracks(@PathVariable int playlistId, @RequestParam List<Integer> trackIds) {
        playlistService.updatePlaylistTracks(playlistId, trackIds);
        return ResponseEntity.ok(ApiResponse.builder()
                .code(1000)
                .message("Update playlist tracks successfully")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePlaylist(@PathVariable int id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .code(1000)
                .message("Delete playlist successfully")
                .build());
    }
}
