package com.popcorn.soundcloudclone.domain.dto.album;

import com.popcorn.soundcloudclone.domain.dto.track.TrackItemResponse;
import com.popcorn.soundcloudclone.domain.dto.user.UserSummaryResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter @Setter
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
}
