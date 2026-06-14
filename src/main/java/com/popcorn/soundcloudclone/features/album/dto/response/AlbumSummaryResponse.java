package com.popcorn.soundcloudclone.features.album.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.popcorn.soundcloudclone.features.track.dto.response.TrackItemResponse;
import com.popcorn.soundcloudclone.features.users.dto.response.UserSummaryResponse;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumSummaryResponse {
    int id;
    String name;
    String coverImageUrl;
    UserSummaryResponse user;
    String description;
    List<String> tags;
    LocalDate releaseDate;
    int likeCount;
    boolean isLiked;
}

