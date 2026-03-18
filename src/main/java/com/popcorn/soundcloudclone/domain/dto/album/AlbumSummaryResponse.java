package com.popcorn.soundcloudclone.domain.dto.album;

import java.time.LocalDate;
import java.util.List;

import com.popcorn.soundcloudclone.domain.dto.track.TrackItemResponse;
import com.popcorn.soundcloudclone.domain.dto.user.UserSummaryResponse;

import lombok.Data;

@Data
public class AlbumSummaryResponse {
    int id;
    String name;
    String coverImageUrl;
    UserSummaryResponse user;
    String description;
    List<String> tags;
    LocalDate releaseDate;
    int likeCount;
    boolean liked;
}
