package com.popcorn.soundcloudclone.features.album.dto.request;

import com.popcorn.soundcloudclone.common.validation.FileExtension;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlbumUpdateRequest {
    String name;

    LocalDate releaseDate;

    @FileExtension(values = ".jpeg, .png, .jpg")
    MultipartFile coverImage;

}
