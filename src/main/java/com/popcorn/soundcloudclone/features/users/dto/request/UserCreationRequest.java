package com.popcorn.soundcloudclone.features.users.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PROTECTED)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreationRequest {
    @NotBlank(message = "INVALID_USERNAME")
    @Size(min = 5, max = 50, message = "INVALID_USERNAME")
    String username;

    @Email(message = "INVALID_EMAIL")
    String email;

    @Size(min = 6, max = 255, message = "INVALID_PASSWORD")
    String password;

    @NotBlank(message = "INVALID_FIRSTNAME")
    private String firstName;

    @NotBlank(message = "INVALID_LASTNAME")
    private String lastName;

}
