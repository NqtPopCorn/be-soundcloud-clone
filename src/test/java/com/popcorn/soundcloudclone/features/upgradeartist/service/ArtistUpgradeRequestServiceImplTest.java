package com.popcorn.soundcloudclone.features.upgradeartist.service;

import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.upgradeartist.entity.ArtistUpgradeRequest;
import com.popcorn.soundcloudclone.features.upgradeartist.mapper.ArtistUpgradeRequestMapper;
import com.popcorn.soundcloudclone.features.upgradeartist.repository.ArtistUpgradeRequestRepository;
import com.popcorn.soundcloudclone.features.upgradeartist.service.impl.ArtistUpgradeRequestServiceImpl;
import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistUpgradeRequestServiceImplTest {

    @Mock
    ArtistUpgradeRequestRepository requestRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ArtistUpgradeRequestMapper mapper;

    @InjectMocks
    ArtistUpgradeRequestServiceImpl service;

    @Test
    void createRequestCreatesPendingRequestForRegularUser() {
        User user = user(7, User.Role.USER, true);
        when(userRepository.findById(7)).thenReturn(Optional.of(user));
        when(requestRepository.existsByUserIdAndStatus(7, ArtistUpgradeRequest.Status.PENDING)).thenReturn(false);
        when(requestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.createRequest(7);

        ArgumentCaptor<ArtistUpgradeRequest> captor = ArgumentCaptor.forClass(ArtistUpgradeRequest.class);
        verify(requestRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isSameAs(user);
        assertThat(captor.getValue().getStatus()).isEqualTo(ArtistUpgradeRequest.Status.PENDING);
    }

    @Test
    void createRequestRejectsDuplicatePendingRequest() {
        User user = user(7, User.Role.USER, true);
        when(userRepository.findById(7)).thenReturn(Optional.of(user));
        when(requestRepository.existsByUserIdAndStatus(7, ArtistUpgradeRequest.Status.PENDING)).thenReturn(true);

        assertThatThrownBy(() -> service.createRequest(7))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("pending");
    }

    @Test
    void createRequestRejectsAlreadyArtistUser() {
        when(userRepository.findById(7)).thenReturn(Optional.of(user(7, User.Role.ARTIST, true)));

        assertThatThrownBy(() -> service.createRequest(7))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("not allowed");
    }

    @Test
    void approvePendingRequestPromotesRequesterToArtist() {
        User requester = user(7, User.Role.USER, true);
        User admin = user(1, User.Role.ADMIN, true);
        ArtistUpgradeRequest request = request(33, requester, ArtistUpgradeRequest.Status.PENDING);
        when(requestRepository.findById(33)).thenReturn(Optional.of(request));
        when(userRepository.findById(1)).thenReturn(Optional.of(admin));
        when(requestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.approve(33, 1);

        assertThat(request.getStatus()).isEqualTo(ArtistUpgradeRequest.Status.APPROVED);
        assertThat(request.getUser().getRole()).isEqualTo(User.Role.ARTIST);
        assertThat(request.getReviewedBy()).isSameAs(admin);
        assertThat(request.getReviewedAt()).isNotNull();
    }

    @Test
    void rejectPendingRequestDoesNotPromoteRequester() {
        User requester = user(7, User.Role.USER, true);
        User admin = user(1, User.Role.ADMIN, true);
        ArtistUpgradeRequest request = request(33, requester, ArtistUpgradeRequest.Status.PENDING);
        when(requestRepository.findById(33)).thenReturn(Optional.of(request));
        when(userRepository.findById(1)).thenReturn(Optional.of(admin));
        when(requestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.reject(33, 1, "Need more profile info");

        assertThat(request.getStatus()).isEqualTo(ArtistUpgradeRequest.Status.REJECTED);
        assertThat(request.getUser().getRole()).isEqualTo(User.Role.USER);
        assertThat(request.getNote()).isEqualTo("Need more profile info");
        assertThat(request.getReviewedBy()).isSameAs(admin);
    }

    @Test
    void approveRejectsProcessedRequest() {
        ArtistUpgradeRequest request = request(33, user(7, User.Role.USER, true), ArtistUpgradeRequest.Status.REJECTED);
        when(requestRepository.findById(33)).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> service.approve(33, 1))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("processed");
    }

    @Test
    void getRequestsReturnsPagedRequestsByStatus() {
        ArtistUpgradeRequest request = request(33, user(7, User.Role.USER, true), ArtistUpgradeRequest.Status.PENDING);
        when(requestRepository.findByStatus(ArtistUpgradeRequest.Status.PENDING, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(request), PageRequest.of(0, 10), 1));

        PageResponse<?> response = service.getRequests(ArtistUpgradeRequest.Status.PENDING, PageRequest.of(0, 10));

        assertThat(response.getItems()).hasSize(1);
    }

    private static User user(int id, User.Role role, boolean active) {
        return User.builder()
                .id(id)
                .username("user" + id)
                .email("user" + id + "@test.local")
                .password("secret")
                .firstName("First")
                .lastName("Last")
                .role(role)
                .active(active)
                .build();
    }

    private static ArtistUpgradeRequest request(int id, User user, ArtistUpgradeRequest.Status status) {
        return ArtistUpgradeRequest.builder()
                .id(id)
                .user(user)
                .status(status)
                .build();
    }
}
