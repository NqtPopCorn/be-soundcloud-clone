package com.popcorn.soundcloudclone.features.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LikeResquest {
    @NotNull(message = "Object id is required")
    Integer objectId;

    @NotNull(message = "liked is required")
    Boolean liked;
}
