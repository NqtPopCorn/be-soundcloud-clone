package com.popcorn.soundcloudclone.domain.dto.track;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class GenreResponse {
    Integer id;
    String name;
//    List<TrackResponse> tracks;
}
