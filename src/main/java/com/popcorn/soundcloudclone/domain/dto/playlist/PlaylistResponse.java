package com.popcorn.soundcloudclone.domain.dto.playlist;

import com.popcorn.soundcloudclone.domain.dto.track.TrackItemResponse;
import com.popcorn.soundcloudclone.domain.dto.user.UserSummaryResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistResponse {
    String name;
    UserSummaryResponse user;
    List<TrackItemResponse> tracks;
}
