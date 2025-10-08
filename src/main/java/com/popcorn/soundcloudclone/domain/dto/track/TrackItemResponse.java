package com.popcorn.soundcloudclone.domain.dto.track;

import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
public class TrackItemResponse extends TrackResponse {

    private Integer position;
}
