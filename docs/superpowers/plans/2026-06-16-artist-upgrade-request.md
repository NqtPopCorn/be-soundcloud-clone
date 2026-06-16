# Artist Upgrade Request Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Require approved artist status before track upload and add an admin-reviewed artist upgrade request workflow.

**Architecture:** Add a focused `features/upgradeartist` module with entity, repository, service, DTOs, mapper, and controller endpoints. Enforce upload authorization in `TrackServiceImpl` so the rule holds even while `SecurityConfig` permits all requests and method security behavior changes.

**Tech Stack:** Spring Boot 3.5, Spring MVC, Spring Security method annotations, Spring Data JPA, MapStruct, Lombok, JUnit 5, Mockito, MockMvc.

---

## File Structure

- Create `src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/entity/ArtistUpgradeRequest.java`: persisted request aggregate and status enum.
- Create `src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/repository/ArtistUpgradeRequestRepository.java`: request lookup, duplicate pending check, pageable status filter.
- Create `src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/dto/ArtistUpgradeRequestResponse.java`: API response for user/admin request views.
- Create `src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/dto/RejectArtistUpgradeRequest.java`: admin rejection note body.
- Create `src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/mapper/ArtistUpgradeRequestMapper.java`: converts entity to response DTO.
- Create `src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/service/ArtistUpgradeRequestService.java`: service contract.
- Create `src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/service/impl/ArtistUpgradeRequestServiceImpl.java`: workflow rules and role transition.
- Create `src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/controller/ArtistUpgradeRequestController.java`: user and admin endpoints.
- Modify `src/main/java/com/popcorn/soundcloudclone/common/exception/ErrorCode.java`: add explicit workflow error codes.
- Modify `src/main/java/com/popcorn/soundcloudclone/features/track/service/impl/TrackServiceImpl.java`: block non-artist upload before file upload.
- Modify `src/main/java/com/popcorn/soundcloudclone/features/track/controller/TrackController.java`: add `hasAnyRole('ARTIST','ADMIN')` to document web-layer policy.
- Test `src/test/java/com/popcorn/soundcloudclone/features/upgradeartist/service/ArtistUpgradeRequestServiceImplTest.java`: request, duplicate, approve, reject, processed request rules.
- Test `src/test/java/com/popcorn/soundcloudclone/features/track/service/TrackServiceImplUploadAuthorizationTest.java`: upload authorization at service boundary.

### Task 1: Artist upgrade request service tests

**Files:**
- Create: `src/test/java/com/popcorn/soundcloudclone/features/upgradeartist/service/ArtistUpgradeRequestServiceImplTest.java`

- [ ] **Step 1: Write the failing service tests**

```java
package com.popcorn.soundcloudclone.features.upgradeartist.service;

import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;
import com.popcorn.soundcloudclone.features.upgradeartist.entity.ArtistUpgradeRequest;
import com.popcorn.soundcloudclone.features.upgradeartist.mapper.ArtistUpgradeRequestMapper;
import com.popcorn.soundcloudclone.features.upgradeartist.repository.ArtistUpgradeRequestRepository;
import com.popcorn.soundcloudclone.features.upgradeartist.service.impl.ArtistUpgradeRequestServiceImpl;
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

    @Mock ArtistUpgradeRequestRepository requestRepository;
    @Mock UserRepository userRepository;
    @Mock ArtistUpgradeRequestMapper mapper;
    @InjectMocks ArtistUpgradeRequestServiceImpl service;

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
                .hasMessageContaining("already");
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
                .hasMessageContaining("already processed");
    }

    @Test
    void getRequestsReturnsPagedRequestsByStatus() {
        ArtistUpgradeRequest request = request(33, user(7, User.Role.USER, true), ArtistUpgradeRequest.Status.PENDING);
        when(requestRepository.findByStatus(ArtistUpgradeRequest.Status.PENDING, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(request), PageRequest.of(0, 10), 1));

        PageResponse<?> response = service.getRequests(ArtistUpgradeRequest.Status.PENDING, PageRequest.of(0, 10));

        assertThat(response.getContent()).hasSize(1);
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
        ArtistUpgradeRequest request = ArtistUpgradeRequest.builder()
                .id(id)
                .user(user)
                .status(status)
                .build();
        return request;
    }
}
```

- [ ] **Step 2: Run tests to verify RED**

Run: `.\mvnw.cmd -Dtest=ArtistUpgradeRequestServiceImplTest test`

Expected: compilation fails because `ArtistUpgradeRequest`, repository, mapper, and service implementation do not exist.

### Task 2: Artist upgrade domain, repository, mapper, and service

**Files:**
- Create: `src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/entity/ArtistUpgradeRequest.java`
- Create: `src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/repository/ArtistUpgradeRequestRepository.java`
- Create: `src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/dto/ArtistUpgradeRequestResponse.java`
- Create: `src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/dto/RejectArtistUpgradeRequest.java`
- Create: `src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/mapper/ArtistUpgradeRequestMapper.java`
- Create: `src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/service/ArtistUpgradeRequestService.java`
- Create: `src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/service/impl/ArtistUpgradeRequestServiceImpl.java`
- Modify: `src/main/java/com/popcorn/soundcloudclone/common/exception/ErrorCode.java`

- [ ] **Step 1: Add workflow error codes**

In `ErrorCode.java`, add these enum constants near the user/security errors:

```java
ARTIST_UPGRADE_REQUEST_NOT_FOUND(109, 404, "Artist upgrade request not found"),
ARTIST_UPGRADE_REQUEST_ALREADY_PENDING(110, 409, "Artist upgrade request is already pending"),
ARTIST_UPGRADE_REQUEST_ALREADY_PROCESSED(111, 409, "Artist upgrade request is already processed"),
ARTIST_UPGRADE_NOT_ALLOWED(112, 403, "Artist upgrade request is not allowed"),
```

- [ ] **Step 2: Add the entity**

Create `ArtistUpgradeRequest.java`:

```java
package com.popcorn.soundcloudclone.features.upgradeartist.entity;

import com.popcorn.soundcloudclone.features.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "artist_upgrade_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistUpgradeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(columnDefinition = "TEXT")
    private String note;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = Status.PENDING;
        }
    }
}
```

- [ ] **Step 3: Add repository**

Create `ArtistUpgradeRequestRepository.java`:

```java
package com.popcorn.soundcloudclone.features.upgradeartist.repository;

import com.popcorn.soundcloudclone.features.upgradeartist.entity.ArtistUpgradeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistUpgradeRequestRepository extends JpaRepository<ArtistUpgradeRequest, Integer> {
    boolean existsByUserIdAndStatus(Integer userId, ArtistUpgradeRequest.Status status);

    Optional<ArtistUpgradeRequest> findFirstByUserIdOrderByCreatedAtDesc(Integer userId);

    Page<ArtistUpgradeRequest> findByStatus(ArtistUpgradeRequest.Status status, Pageable pageable);
}
```

- [ ] **Step 4: Add DTOs and mapper**

Create `ArtistUpgradeRequestResponse.java`:

```java
package com.popcorn.soundcloudclone.features.upgradeartist.dto;

import com.popcorn.soundcloudclone.features.users.dto.response.UserSummaryResponse;
import com.popcorn.soundcloudclone.features.upgradeartist.entity.ArtistUpgradeRequest;
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
```

Create `RejectArtistUpgradeRequest.java`:

```java
package com.popcorn.soundcloudclone.features.upgradeartist.dto;

import lombok.Data;

@Data
public class RejectArtistUpgradeRequest {
    private String note;
}
```

Create `ArtistUpgradeRequestMapper.java`:

```java
package com.popcorn.soundcloudclone.features.upgradeartist.mapper;

import com.popcorn.soundcloudclone.features.users.mapper.UserMapper;
import com.popcorn.soundcloudclone.features.upgradeartist.dto.ArtistUpgradeRequestResponse;
import com.popcorn.soundcloudclone.features.upgradeartist.entity.ArtistUpgradeRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface ArtistUpgradeRequestMapper {
    ArtistUpgradeRequestResponse toResponse(ArtistUpgradeRequest request);
}
```

- [ ] **Step 5: Add service contract and implementation**

Create `ArtistUpgradeRequestService.java`:

```java
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
```

Create `ArtistUpgradeRequestServiceImpl.java`:

```java
package com.popcorn.soundcloudclone.features.upgradeartist.service.impl;

import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.common.exception.ErrorCode;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;
import com.popcorn.soundcloudclone.features.upgradeartist.dto.ArtistUpgradeRequestResponse;
import com.popcorn.soundcloudclone.features.upgradeartist.entity.ArtistUpgradeRequest;
import com.popcorn.soundcloudclone.features.upgradeartist.mapper.ArtistUpgradeRequestMapper;
import com.popcorn.soundcloudclone.features.upgradeartist.repository.ArtistUpgradeRequestRepository;
import com.popcorn.soundcloudclone.features.upgradeartist.service.ArtistUpgradeRequestService;
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
    public PageResponse<ArtistUpgradeRequestResponse> getRequests(ArtistUpgradeRequest.Status status, Pageable pageable) {
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
```

- [ ] **Step 6: Run service tests to verify GREEN**

Run: `.\mvnw.cmd -Dtest=ArtistUpgradeRequestServiceImplTest test`

Expected: tests pass.

- [ ] **Step 7: Commit domain and service**

```bash
git add src/main/java/com/popcorn/soundcloudclone/common/exception/ErrorCode.java src/main/java/com/popcorn/soundcloudclone/features/upgradeartist src/test/java/com/popcorn/soundcloudclone/features/upgradeartist/service/ArtistUpgradeRequestServiceImplTest.java
git commit -m "feat: add artist upgrade request workflow"
```

### Task 3: Upload authorization tests and guard

**Files:**
- Create: `src/test/java/com/popcorn/soundcloudclone/features/track/service/TrackServiceImplUploadAuthorizationTest.java`
- Modify: `src/main/java/com/popcorn/soundcloudclone/features/track/service/impl/TrackServiceImpl.java`
- Modify: `src/main/java/com/popcorn/soundcloudclone/features/track/controller/TrackController.java`

- [ ] **Step 1: Write failing upload authorization tests**

```java
package com.popcorn.soundcloudclone.features.track.service;

import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.features.track.dto.request.TrackCreationRequest;
import com.popcorn.soundcloudclone.features.track.mapper.TrackMapper;
import com.popcorn.soundcloudclone.features.track.repository.TrackPlayRepository;
import com.popcorn.soundcloudclone.features.track.repository.TrackRepository;
import com.popcorn.soundcloudclone.features.track.service.impl.TrackServiceImpl;
import com.popcorn.soundcloudclone.features.media.service.UploadService;
import com.popcorn.soundcloudclone.common.security.CurrentUserContext;
import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrackServiceImplUploadAuthorizationTest {

    @Mock TrackRepository trackRepository;
    @Mock UserRepository userRepository;
    @Mock UploadService uploadService;
    @Mock TrackMapper trackMapper;
    @Mock TrackPlayRepository trackPlayRepository;
    @Mock CurrentUserContext currentUserContext;
    @InjectMocks TrackServiceImpl service;

    @Test
    void regularUserCannotCreateTrack() {
        User user = user(User.Role.USER, true);
        when(userRepository.findById(7)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.createTrack(7, new TrackCreationRequest()))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("upload");

        verify(trackRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(uploadService, never()).upload(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void inactiveArtistCannotCreateTrack() {
        User user = user(User.Role.ARTIST, false);
        when(userRepository.findById(7)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.createTrack(7, new TrackCreationRequest()))
                .isInstanceOf(ApplicationException.class);
    }

    private static User user(User.Role role, boolean active) {
        return User.builder()
                .id(7)
                .username("artist")
                .email("artist@test.local")
                .password("secret")
                .firstName("First")
                .lastName("Last")
                .role(role)
                .active(active)
                .build();
    }
}
```

- [ ] **Step 2: Run upload tests to verify RED**

Run: `.\mvnw.cmd -Dtest=TrackServiceImplUploadAuthorizationTest test`

Expected: `regularUserCannotCreateTrack` fails because `createTrack` currently allows any found user to continue.

- [ ] **Step 3: Add service-level upload guard**

In `TrackServiceImpl.createTrack`, replace the current direct artist assignment:

```java
track.setArtist(findUserOrThrow(userId));
```

with:

```java
User artist = findUserOrThrow(userId);
ensureCanUploadTrack(artist);
track.setArtist(artist);
```

Add this private method in `TrackServiceImpl`:

```java
private void ensureCanUploadTrack(User user) {
    if (!user.isActive() || (user.getRole() != User.Role.ARTIST && user.getRole() != User.Role.ADMIN)) {
        throw new ApplicationException("Only active artists can upload tracks", ErrorCode.FORBIDDEN);
    }
}
```

Add imports if missing:

```java
import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.common.exception.ErrorCode;
```

- [ ] **Step 4: Add controller policy annotation**

Change `TrackController.addTrack` annotation from:

```java
@PreAuthorize("isAuthenticated()")
```

to:

```java
@PreAuthorize("hasAnyRole('ARTIST', 'ADMIN')")
```

- [ ] **Step 5: Run upload tests to verify GREEN**

Run: `.\mvnw.cmd -Dtest=TrackServiceImplUploadAuthorizationTest test`

Expected: tests pass.

- [ ] **Step 6: Commit upload guard**

```bash
git add src/main/java/com/popcorn/soundcloudclone/features/track/service/impl/TrackServiceImpl.java src/main/java/com/popcorn/soundcloudclone/features/track/controller/TrackController.java src/test/java/com/popcorn/soundcloudclone/features/track/service/TrackServiceImplUploadAuthorizationTest.java
git commit -m "feat: restrict track uploads to artists"
```

### Task 4: Artist upgrade request HTTP endpoints

**Files:**
- Create: `src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/controller/ArtistUpgradeRequestController.java`
- Create: `src/test/java/com/popcorn/soundcloudclone/features/upgradeartist/controller/ArtistUpgradeRequestControllerTest.java`

- [ ] **Step 1: Write failing controller tests**

```java
package com.popcorn.soundcloudclone.features.upgradeartist.controller;

import com.popcorn.soundcloudclone.common.security.MyUserDetails;
import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.upgradeartist.dto.ArtistUpgradeRequestResponse;
import com.popcorn.soundcloudclone.features.upgradeartist.service.ArtistUpgradeRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class ArtistUpgradeRequestControllerTest {

    @Test
    void userCreatesUpgradeRequest() throws Exception {
        ArtistUpgradeRequestService service = mock(ArtistUpgradeRequestService.class);
        ArtistUpgradeRequestResponse response = new ArtistUpgradeRequestResponse();
        response.setId(55);
        when(service.createRequest(7)).thenReturn(response);

        MockMvc mockMvc = standaloneSetup(new ArtistUpgradeRequestController(service)).build();

        mockMvc.perform(post("/user/me/artist-upgrade-request")
                        .with(authentication(new MyUserDetails(user(7, User.Role.USER)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.result.id").value(55));
    }

    @Test
    void adminApprovesUpgradeRequest() throws Exception {
        ArtistUpgradeRequestService service = mock(ArtistUpgradeRequestService.class);
        ArtistUpgradeRequestResponse response = new ArtistUpgradeRequestResponse();
        response.setId(55);
        when(service.approve(55, 1)).thenReturn(response);

        MockMvc mockMvc = standaloneSetup(new ArtistUpgradeRequestController(service)).build();

        mockMvc.perform(patch("/users/artist-upgrade-requests/55/approve")
                        .with(authentication(new MyUserDetails(user(1, User.Role.ADMIN)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Artist upgrade request approved"));

        verify(service).approve(55, 1);
    }

    @Test
    void adminRejectsUpgradeRequestWithNote() throws Exception {
        ArtistUpgradeRequestService service = mock(ArtistUpgradeRequestService.class);
        ArtistUpgradeRequestResponse response = new ArtistUpgradeRequestResponse();
        response.setId(55);
        when(service.reject(eq(55), eq(1), eq("Need more info"))).thenReturn(response);

        MockMvc mockMvc = standaloneSetup(new ArtistUpgradeRequestController(service)).build();

        mockMvc.perform(patch("/users/artist-upgrade-requests/55/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":\"Need more info\"}")
                        .with(authentication(new MyUserDetails(user(1, User.Role.ADMIN)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Artist upgrade request rejected"));
    }

    private static User user(int id, User.Role role) {
        return User.builder()
                .id(id)
                .username("user" + id)
                .email("user" + id + "@test.local")
                .password("secret")
                .firstName("First")
                .lastName("Last")
                .role(role)
                .active(true)
                .build();
    }
}
```

If `spring-security-test` is missing, add this test dependency to `pom.xml` before running the test:

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

- [ ] **Step 2: Run controller tests to verify RED**

Run: `.\mvnw.cmd -Dtest=ArtistUpgradeRequestControllerTest test`

Expected: compilation fails because `ArtistUpgradeRequestController` does not exist.

- [ ] **Step 3: Add controller**

Create `ArtistUpgradeRequestController.java`:

```java
package com.popcorn.soundcloudclone.features.upgradeartist.controller;

import com.popcorn.soundcloudclone.common.response.ApiResponse;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.common.security.MyUserDetails;
import com.popcorn.soundcloudclone.features.upgradeartist.dto.ArtistUpgradeRequestResponse;
import com.popcorn.soundcloudclone.features.upgradeartist.dto.RejectArtistUpgradeRequest;
import com.popcorn.soundcloudclone.features.upgradeartist.entity.ArtistUpgradeRequest;
import com.popcorn.soundcloudclone.features.upgradeartist.service.ArtistUpgradeRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ArtistUpgradeRequestController {
    private final ArtistUpgradeRequestService service;

    @PostMapping("/user/me/artist-upgrade-request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ArtistUpgradeRequestResponse>> createRequest(
            @AuthenticationPrincipal MyUserDetails userDetails) {
        var result = service.createRequest(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.<ArtistUpgradeRequestResponse>builder()
                .statusCode(200)
                .message("Artist upgrade request created")
                .result(result)
                .build());
    }

    @GetMapping("/user/me/artist-upgrade-request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ArtistUpgradeRequestResponse>> getLatestRequest(
            @AuthenticationPrincipal MyUserDetails userDetails) {
        var result = service.getLatestRequest(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.<ArtistUpgradeRequestResponse>builder()
                .statusCode(200)
                .message("Success")
                .result(result)
                .build());
    }

    @GetMapping("/users/artist-upgrade-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ArtistUpgradeRequestResponse>>> getRequests(
            @RequestParam(required = false) ArtistUpgradeRequest.Status status,
            @PageableDefault Pageable pageable) {
        var result = service.getRequests(status, pageable);
        return ResponseEntity.ok(ApiResponse.<PageResponse<ArtistUpgradeRequestResponse>>builder()
                .statusCode(200)
                .message("Success")
                .result(result)
                .build());
    }

    @PatchMapping("/users/artist-upgrade-requests/{requestId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ArtistUpgradeRequestResponse>> approve(
            @PathVariable int requestId,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        var result = service.approve(requestId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.<ArtistUpgradeRequestResponse>builder()
                .statusCode(200)
                .message("Artist upgrade request approved")
                .result(result)
                .build());
    }

    @PatchMapping("/users/artist-upgrade-requests/{requestId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ArtistUpgradeRequestResponse>> reject(
            @PathVariable int requestId,
            @AuthenticationPrincipal MyUserDetails userDetails,
            @RequestBody(required = false) RejectArtistUpgradeRequest request) {
        String note = request == null ? null : request.getNote();
        var result = service.reject(requestId, userDetails.getUserId(), note);
        return ResponseEntity.ok(ApiResponse.<ArtistUpgradeRequestResponse>builder()
                .statusCode(200)
                .message("Artist upgrade request rejected")
                .result(result)
                .build());
    }
}
```

- [ ] **Step 4: Run controller tests to verify GREEN**

Run: `.\mvnw.cmd -Dtest=ArtistUpgradeRequestControllerTest test`

Expected: tests pass.

- [ ] **Step 5: Commit HTTP endpoints**

```bash
git add pom.xml src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/controller/ArtistUpgradeRequestController.java src/test/java/com/popcorn/soundcloudclone/features/upgradeartist/controller/ArtistUpgradeRequestControllerTest.java
git commit -m "feat: expose artist upgrade request endpoints"
```

### Task 5: Full verification

**Files:**
- Review: `src/main/java/com/popcorn/soundcloudclone/features/upgradeartist/**`
- Review: `src/main/java/com/popcorn/soundcloudclone/features/track/**`
- Review: `src/test/java/com/popcorn/soundcloudclone/features/upgradeartist/**`
- Review: `src/test/java/com/popcorn/soundcloudclone/features/track/service/TrackServiceImplUploadAuthorizationTest.java`

- [ ] **Step 1: Run focused tests**

Run:

```powershell
.\mvnw.cmd -Dtest=ArtistUpgradeRequestServiceImplTest,ArtistUpgradeRequestControllerTest,TrackServiceImplUploadAuthorizationTest test
```

Expected: all selected tests pass.

- [ ] **Step 2: Run full test suite**

Run:

```powershell
.\mvnw.cmd test
```

Expected: build succeeds and all tests pass.

- [ ] **Step 3: Check working tree**

Run:

```powershell
git status --short
```

Expected: only intentional uncommitted files remain from pre-existing user work, or a clean tree if no such work exists.

- [ ] **Step 4: Final commit if verification required fixes**

If verification required code changes, commit the exact touched files:

```bash
git add <verified-files>
git commit -m "test: verify artist upgrade request workflow"
```

Use explicit file paths in the actual command so unrelated user changes are not staged.

## Self-Review

- Spec coverage: upload blocked for regular users is covered in Task 3; user request creation/latest request is covered in Tasks 1, 2, and 4; admin list/approve/reject is covered in Tasks 1, 2, and 4; processed request protection is covered in Task 1.
- Placeholder scan: plan contains concrete paths, commands, expected failures, and implementation snippets for each code step.
- Type consistency: status enum is `ArtistUpgradeRequest.Status`; service method names are `createRequest`, `getLatestRequest`, `getRequests`, `approve`, and `reject`; DTO names and controller paths match across tasks.
