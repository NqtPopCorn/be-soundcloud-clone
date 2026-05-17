package com.popcorn.soundcloudclone.features.album.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlbumAddTrackRequest {
    int trackId;
    int position;
}
