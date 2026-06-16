package com.popcorn.soundcloudclone.common.security.throttle;

import com.popcorn.soundcloudclone.features.auth.controller.AuthController;
import com.popcorn.soundcloudclone.features.auth.dto.response.AuthResponse;
import com.popcorn.soundcloudclone.features.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class SpamThrottleFilterTest {

    @Test
    void throttlesLoginAfterFiveRequestsFromSameClient() throws Exception {
        AuthService authService = mock(AuthService.class);
        when(authService.authenticate(anyString(), anyString()))
                .thenReturn(AuthResponse.builder()
                        .accessToken("access-token")
                        .refreshToken("refresh-token")
                        .refreshTokenExpiresIn(3600)
                        .build());

        MockMvc mockMvc = standaloneSetup(new AuthController(authService))
                .addFilters(SpamThrottleFilter.createDefault())
                .build();

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"demo\",\"password\":\"secret\"}")
                            .with(request -> {
                                request.setRemoteAddr("10.0.0.7");
                                return request;
                            }))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"demo\",\"password\":\"secret\"}")
                        .with(request -> {
                            request.setRemoteAddr("10.0.0.7");
                            return request;
                        }))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.statusCode").value(429))
                .andExpect(jsonPath("$.message").value("Too many requests"));
    }
}
