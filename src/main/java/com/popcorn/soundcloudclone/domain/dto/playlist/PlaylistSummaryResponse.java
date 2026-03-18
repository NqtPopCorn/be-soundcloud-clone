package com.popcorn.soundcloudclone.domain.dto.playlist;

import com.popcorn.soundcloudclone.domain.dto.user.UserSummaryResponse;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class PlaylistSummaryResponse {
    private int id;
    private String name;
    private int trackCount;
    private String imageUrl;
    private boolean isPublic;
    private boolean liked;
    private UserSummaryResponse user;
}
