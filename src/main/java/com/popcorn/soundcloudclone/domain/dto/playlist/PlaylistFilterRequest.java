package com.popcorn.soundcloudclone.domain.dto.playlist;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaylistFilterRequest {
    String keyword;
    Integer userId;
}
