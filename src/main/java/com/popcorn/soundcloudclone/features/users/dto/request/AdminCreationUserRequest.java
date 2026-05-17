package com.popcorn.soundcloudclone.features.users.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@Setter
@Getter
public class AdminCreationUserRequest extends UserCreationRequest {

    @Pattern(regexp = "^ADMIN|USER|ARTIST$", message = "INVALID_ROLE")
    private String role;

    private boolean active = true;
}
