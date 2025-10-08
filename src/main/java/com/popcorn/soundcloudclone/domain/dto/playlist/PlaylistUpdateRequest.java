package com.popcorn.soundcloudclone.domain.dto.playlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistUpdateRequest {
    @NotBlank
    String name;

    @NotNull
    Boolean isPublic;
}
