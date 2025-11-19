package com.popcorn.soundcloudclone.domain.dto.playlist;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaylistSummaryResponse {
    int id;
    String name;
    int trackCount;
    String imageUrl;
    boolean isPublic;
}
