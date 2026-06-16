package com.popcorn.soundcloudclone.features.upgradeartist.controller;

import com.popcorn.soundcloudclone.common.security.MyUserDetails;
import com.popcorn.soundcloudclone.features.upgradeartist.dto.ArtistUpgradeRequestResponse;
import com.popcorn.soundcloudclone.features.upgradeartist.service.ArtistUpgradeRequestService;
import com.popcorn.soundcloudclone.features.users.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class ArtistUpgradeRequestControllerTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void userCreatesUpgradeRequest() throws Exception {
        ArtistUpgradeRequestService service = mock(ArtistUpgradeRequestService.class);
        ArtistUpgradeRequestResponse response = new ArtistUpgradeRequestResponse();
        response.setId(55);
        when(service.createRequest(7)).thenReturn(response);

        MockMvc mockMvc = mockMvc(service);

        mockMvc.perform(post("/user/me/artist-upgrade-request")
                        .with(authenticated(user(7, User.Role.USER))))
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

        MockMvc mockMvc = mockMvc(service);

        mockMvc.perform(patch("/users/artist-upgrade-requests/55/approve")
                        .with(authenticated(user(1, User.Role.ADMIN))))
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

        MockMvc mockMvc = mockMvc(service);

        mockMvc.perform(patch("/users/artist-upgrade-requests/55/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":\"Need more info\"}")
                        .with(authenticated(user(1, User.Role.ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Artist upgrade request rejected"));
    }

    private static RequestPostProcessor authenticated(User user) {
        return request -> {
            SecurityContextHolder.getContext().setAuthentication(auth(user));
            return request;
        };
    }

    private static TestingAuthenticationToken auth(User user) {
        MyUserDetails userDetails = new MyUserDetails(user);
        return new TestingAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private static MockMvc mockMvc(ArtistUpgradeRequestService service) {
        return standaloneSetup(new ArtistUpgradeRequestController(service))
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
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
