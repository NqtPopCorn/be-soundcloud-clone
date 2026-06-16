package com.popcorn.soundcloudclone.features.track.service;

import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.common.security.CurrentUserContext;
import com.popcorn.soundcloudclone.features.genre.repository.GenreRepository;
import com.popcorn.soundcloudclone.features.media.service.UploadService;
import com.popcorn.soundcloudclone.features.track.dto.request.TrackCreationRequest;
import com.popcorn.soundcloudclone.features.track.entity.Track;
import com.popcorn.soundcloudclone.features.track.mapper.TrackMapper;
import com.popcorn.soundcloudclone.features.track.repository.TrackPlayRepository;
import com.popcorn.soundcloudclone.features.track.repository.TrackRepository;
import com.popcorn.soundcloudclone.features.track.service.impl.TrackServiceImpl;
import com.popcorn.soundcloudclone.features.users.entity.User;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrackServiceImplUploadAuthorizationTest {

    @Mock
    TrackRepository trackRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    GenreRepository genreRepository;

    @Mock
    UploadService uploadService;

    @Mock
    TrackMapper trackMapper;

    @Mock
    TrackPlayRepository trackPlayRepository;

    @Mock
    CurrentUserContext currentUserContext;

    @Mock
    StringRedisTemplate redisTemplate;

    @InjectMocks
    TrackServiceImpl service;

    @Test
    void regularUserCannotCreateTrack() {
        User user = user(User.Role.USER, true);
        TrackCreationRequest request = validRequest();
        when(userRepository.findById(7)).thenReturn(Optional.of(user));
        when(trackMapper.toTrack(request)).thenReturn(new Track());

        assertThatThrownBy(() -> service.createTrack(7, request))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("upload");

        verify(trackRepository, never()).save(any());
        verify(uploadService, never()).upload(any(), anyString(), anyString());
    }

    @Test
    void inactiveArtistCannotCreateTrack() {
        User user = user(User.Role.ARTIST, false);
        TrackCreationRequest request = validRequest();
        when(userRepository.findById(7)).thenReturn(Optional.of(user));
        when(trackMapper.toTrack(request)).thenReturn(new Track());

        assertThatThrownBy(() -> service.createTrack(7, request))
                .isInstanceOf(ApplicationException.class);
    }

    private static TrackCreationRequest validRequest() {
        TrackCreationRequest request = new TrackCreationRequest();
        request.setName("Song");
        request.setPrivacy("PUBLIC");
        request.setDuration(180);
        request.setGenreIds(List.of(1));
        request.setAudioUpload(org.mockito.Mockito.mock(MultipartFile.class));
        request.setImageUpload(org.mockito.Mockito.mock(MultipartFile.class));
        return request;
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
