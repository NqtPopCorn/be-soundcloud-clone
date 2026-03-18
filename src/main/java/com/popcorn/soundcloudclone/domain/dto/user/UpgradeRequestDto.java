package com.popcorn.soundcloudclone.domain.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpgradeRequestDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Integer id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    UserSummaryResponse user;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    LocalDateTime createdAt;

}
