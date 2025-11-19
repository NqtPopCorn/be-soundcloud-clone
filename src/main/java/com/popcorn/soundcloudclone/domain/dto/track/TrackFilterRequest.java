package com.popcorn.soundcloudclone.domain.dto.track;

import jakarta.annotation.Nullable;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackFilterRequest {

    private String keyword;
    private String artistName;
}
