package com.popcorn.soundcloudclone.features.upgradeartist.service;

import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.upgradeartist.dto.ArtistUpgradeRequestResponse;
import com.popcorn.soundcloudclone.features.upgradeartist.entity.ArtistUpgradeRequest;
import org.springframework.data.domain.Pageable;

public interface ArtistUpgradeRequestService {
    ArtistUpgradeRequestResponse createRequest(int userId);

    ArtistUpgradeRequestResponse getLatestRequest(int userId);

    PageResponse<ArtistUpgradeRequestResponse> getRequests(ArtistUpgradeRequest.Status status, Pageable pageable);

    ArtistUpgradeRequestResponse approve(int requestId, int adminId);

    ArtistUpgradeRequestResponse reject(int requestId, int adminId, String note);
}
