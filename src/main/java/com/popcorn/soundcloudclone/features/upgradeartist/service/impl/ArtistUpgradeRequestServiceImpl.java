package com.popcorn.soundcloudclone.features.upgradeartist.service.impl;

import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.common.exception.ErrorCode;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.upgradeartist.dto.ArtistUpgradeRequestResponse;
import com.popcorn.soundcloudclone.features.upgradeartist.entity.ArtistUpgradeRequest;
import com.popcorn.soundcloudclone.features.upgradeartist.mapper.ArtistUpgradeRequestMapper;
import com.popcorn.soundcloudclone.features.upgradeartist.repository.ArtistUpgradeRequestRepository;
import com.popcorn.soundcloudclone.features.upgradeartist.service.ArtistUpgradeRequestService;
import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class ArtistUpgradeRequestServiceImpl implements ArtistUpgradeRequestService {
    private final ArtistUpgradeRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ArtistUpgradeRequestMapper mapper;

    @Override
    public ArtistUpgradeRequestResponse createRequest(int userId) {
        User user = findUser(userId);
        if (!user.isActive() || user.getRole() != User.Role.USER) {
            throw new ApplicationException(ErrorCode.ARTIST_UPGRADE_NOT_ALLOWED);
        }
        if (requestRepository.existsByUserIdAndStatus(userId, ArtistUpgradeRequest.Status.PENDING)) {
            throw new ApplicationException(ErrorCode.ARTIST_UPGRADE_REQUEST_ALREADY_PENDING);
        }

        ArtistUpgradeRequest request = ArtistUpgradeRequest.builder()
                .user(user)
                .status(ArtistUpgradeRequest.Status.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        return mapper.toResponse(requestRepository.save(request));
    }

    @Override
    public ArtistUpgradeRequestResponse getLatestRequest(int userId) {
        ArtistUpgradeRequest request = requestRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.ARTIST_UPGRADE_REQUEST_NOT_FOUND));
        return mapper.toResponse(request);
    }

    @Override
    public PageResponse<ArtistUpgradeRequestResponse> getRequests(ArtistUpgradeRequest.Status status,
            Pageable pageable) {
        var page = status == null
                ? requestRepository.findAll(pageable)
                : requestRepository.findByStatus(status, pageable);
        return PageResponse.from(page.map(mapper::toResponse));
    }

    @Override
    public ArtistUpgradeRequestResponse approve(int requestId, int adminId) {
        ArtistUpgradeRequest request = findPendingRequest(requestId);
        User admin = findUser(adminId);
        request.setStatus(ArtistUpgradeRequest.Status.APPROVED);
        request.setReviewedBy(admin);
        request.setReviewedAt(LocalDateTime.now());
        request.getUser().setRole(User.Role.ARTIST);
        return mapper.toResponse(requestRepository.save(request));
    }

    @Override
    public ArtistUpgradeRequestResponse reject(int requestId, int adminId, String note) {
        ArtistUpgradeRequest request = findPendingRequest(requestId);
        User admin = findUser(adminId);
        request.setStatus(ArtistUpgradeRequest.Status.REJECTED);
        request.setReviewedBy(admin);
        request.setReviewedAt(LocalDateTime.now());
        request.setNote(note);
        return mapper.toResponse(requestRepository.save(request));
    }

    private ArtistUpgradeRequest findPendingRequest(int requestId) {
        ArtistUpgradeRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.ARTIST_UPGRADE_REQUEST_NOT_FOUND));
        if (request.getStatus() != ArtistUpgradeRequest.Status.PENDING) {
            throw new ApplicationException(ErrorCode.ARTIST_UPGRADE_REQUEST_ALREADY_PROCESSED);
        }
        return request;
    }

    private User findUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }
}
