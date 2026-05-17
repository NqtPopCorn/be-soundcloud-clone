package com.popcorn.soundcloudclone.features.track.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
public class TrackItemResponse extends TrackResponse {

    private Integer position;
}
