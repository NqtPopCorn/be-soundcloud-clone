package com.popcorn.soundcloudclone.features.genre.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreRequest {
    @NotBlank(message = "Genre name is required")
    private String genreName;
}
