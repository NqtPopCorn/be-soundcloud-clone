package com.popcorn.soundcloudclone.features.track.dto.response;

import com.popcorn.soundcloudclone.features.track.entity.Track;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
public class ArtistTrackResponse extends TrackResponse {
    private Track.Privacy privacy;
}
