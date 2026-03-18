package com.popcorn.soundcloudclone.controller;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.track.GenreResponse;
import com.popcorn.soundcloudclone.domain.service.GenreService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    // TODO: only admin can add genre
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GenreResponse>> addGenre(@RequestParam String genreName) {
        var genre = genreService.create(genreName);
        return ResponseEntity.ok(ApiResponse.<GenreResponse>builder()
                .statusCode(200)
                .message("Add genre successfully!")
                .result(genre)
                .build());
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<GenreResponse>>> getGenres() {
        var genre = genreService.findAll();
        return ResponseEntity.ok(ApiResponse.<List<GenreResponse>>builder()
                .statusCode(200)
                .message("Success")
                .result(genre)
                .build());
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteGenre(@PathVariable int id) {
        genreService.delete(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .statusCode(200)
                .message("Delete genre successfully!")
                .build());
    }
}
