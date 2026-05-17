package com.popcorn.soundcloudclone.features.track.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.popcorn.soundcloudclone.features.track.entity.Track;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackQueryRequest {
    private String keyword;
    private String genre;
    private Integer albumId;
    private Integer playlistId;
    private Integer artistId;
    private Track.Privacy privacy;

}
