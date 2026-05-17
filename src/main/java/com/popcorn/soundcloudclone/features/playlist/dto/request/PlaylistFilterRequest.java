package com.popcorn.soundcloudclone.features.playlist.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaylistFilterRequest {
    String keyword;
    Integer userId;
}
