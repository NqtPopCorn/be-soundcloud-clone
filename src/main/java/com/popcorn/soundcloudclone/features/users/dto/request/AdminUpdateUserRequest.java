package com.popcorn.soundcloudclone.features.users.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@Setter
@Getter
public class AdminUpdateUserRequest {

    @Size(min = 6, max = 255, message = "INVALID_PASSWORD")
    String password;

    @Pattern(regexp = "^ADMIN|USER|ARTIST$", message = "INVALID_ROLE")
    private String role;

    @NotNull(message = "ACTIVE_STATUS_REQUIRED")
    private Boolean active;
}
