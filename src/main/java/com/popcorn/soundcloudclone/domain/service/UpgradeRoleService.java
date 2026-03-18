package com.popcorn.soundcloudclone.domain.service;

import com.popcorn.soundcloudclone.domain.dto.user.UpgradeRequestDto;

import java.util.List;

public interface UpgradeRoleService {
    UpgradeRequestDto createUpgradeRequest(int userId);

    void acceptUpgradeRequest(int requestId);

    void declineUpgradeRequest(int requestId);

    List<UpgradeRequestDto> getUpgradeRequests();
}
