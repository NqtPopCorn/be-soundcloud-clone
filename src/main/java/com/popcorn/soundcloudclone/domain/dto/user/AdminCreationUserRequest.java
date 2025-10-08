package com.popcorn.soundcloudclone.domain.dto.user;

import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@Setter
@Getter
public class AdminCreationUserRequest extends UserCreationRequest {

    @Pattern(regexp = "^ADMIN|USER|ARTIST$", message = "INVALID_ROLE")
    private String role;
}
