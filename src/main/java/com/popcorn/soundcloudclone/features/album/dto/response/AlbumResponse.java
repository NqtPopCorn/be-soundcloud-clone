package com.popcorn.soundcloudclone.features.album.dto.response;

import com.popcorn.soundcloudclone.features.track.dto.response.TrackItemResponse;
import com.popcorn.soundcloudclone.features.users.dto.response.UserSummaryResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class AlbumResponse {
    int id;
    String name;
    String coverImageUrl;
    UserSummaryResponse user;
    String description;
    List<String> tags;
    LocalDate releaseDate;
    List<TrackItemResponse> tracks;
    int likeCount;
    boolean liked;
}

