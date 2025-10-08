package com.popcorn.soundcloudclone.domain.dto.album;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlbumCreationRequest {
    @NotBlank(message = "INVALID_FIELD:name")
    String name;

}
