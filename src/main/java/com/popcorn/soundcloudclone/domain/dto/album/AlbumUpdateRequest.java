package com.popcorn.soundcloudclone.domain.dto.album;

import com.popcorn.soundcloudclone.domain.dto.validation.FileExtension;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlbumUpdateRequest {
    @NotBlank
    String name;

    LocalDate releaseDate;

    @FileExtension(values = ".jpeg, .png, .jpg")
    MultipartFile coverImage;


}
