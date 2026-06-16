package com.popcorn.soundcloudclone.features.upgradeartist.dto;

import com.popcorn.soundcloudclone.features.upgradeartist.entity.ArtistUpgradeRequest;
import com.popcorn.soundcloudclone.features.users.dto.response.UserSummaryResponse;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArtistUpgradeRequestResponse {
    private Integer id;
    private UserSummaryResponse user;
    private ArtistUpgradeRequest.Status status;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private UserSummaryResponse reviewedBy;
    private String note;
}
