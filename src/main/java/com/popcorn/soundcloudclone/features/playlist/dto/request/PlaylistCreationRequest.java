package com.popcorn.soundcloudclone.features.playlist.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistCreationRequest {
    @NotBlank
    String name;

    @NotNull
    Boolean isPublic;

    List<Integer> trackIds = new ArrayList<>();
}
