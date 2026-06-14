package com.popcorn.soundcloudclone.features.genre.dto.response;

import com.popcorn.soundcloudclone.features.track.dto.response.TrackResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class GenreResponse {
    Integer id;
    String name;
//    List<TrackResponse> tracks;
}


