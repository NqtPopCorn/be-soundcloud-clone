package com.popcorn.soundcloudclone.domain.dto.album;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlbumFilterRequestDto {
    String keyword;
    Integer artistId;
}
