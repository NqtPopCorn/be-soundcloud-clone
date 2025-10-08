package com.popcorn.soundcloudclone.domain.dto.track;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
public class ArtistTrackResponse extends TrackResponse {
    private String privacy;
}
