package com.popcorn.soundcloudclone.features.playlist.dto.response;

import com.popcorn.soundcloudclone.features.track.dto.response.TrackItemResponse;
import com.popcorn.soundcloudclone.features.users.dto.response.UserSummaryResponse;

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
