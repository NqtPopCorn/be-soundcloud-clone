package com.popcorn.soundcloudclone.domain.dto.album;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlbumAddTrackRequest {
    int trackId;
    int position;
}
