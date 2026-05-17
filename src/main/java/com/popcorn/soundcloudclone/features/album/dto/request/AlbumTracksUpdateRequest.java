package com.popcorn.soundcloudclone.features.album.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AlbumTracksUpdateRequest {
    @NotEmpty
    private List<Integer> trackIds;
}
