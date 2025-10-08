package com.popcorn.soundcloudclone.domain.dto.user;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class UserUpdateRequest {
    @NotBlank(message = "INVALID_USERNAME")
    String username;

    @Email(message = "INVALID_EMAIL")
    String email;

    @Nullable
    @Size(min = 6, max = 255, message = "INVALID_PASSWORD")
    String password;

    @NotBlank(message = "INVALID_FIRSTNAME")
    private String firstName;

    @NotBlank(message = "INVALID_LASTNAME")
    private String lastName;

    MultipartFile avatarUpload, backgroundUpload;

//    private boolean deleteAvatar = false;

    private String city;
    private String country;
    private String stageName;
    private String bio;
}
