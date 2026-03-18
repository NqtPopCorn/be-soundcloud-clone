package com.popcorn.soundcloudclone.domain.dto.playlist;

import com.popcorn.soundcloudclone.domain.dto.track.TrackItemResponse;
import com.popcorn.soundcloudclone.domain.dto.user.UserSummaryResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistResponse extends PlaylistSummaryResponse {
    UserSummaryResponse user;
    List<TrackItemResponse> tracks;
}
