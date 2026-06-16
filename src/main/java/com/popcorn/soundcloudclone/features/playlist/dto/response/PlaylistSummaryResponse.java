package com.popcorn.soundcloudclone.features.playlist.dto.response;

import com.popcorn.soundcloudclone.features.users.dto.response.UserSummaryResponse;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PlaylistSummaryResponse {
    private int id;
    private String name;
    private int trackCount;
    private int likeCount;
    private String imageUrl;
    private boolean isPublic;
    private boolean liked;
    private UserSummaryResponse user;
}

