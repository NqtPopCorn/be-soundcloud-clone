package com.popcorn.soundcloudclone.domain.dto.track;

import com.popcorn.soundcloudclone.domain.dto.validation.FileExtension;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrackCreationRequest {
    @NotBlank(message = "INVALID_TRACK_NAME")
    String name;

    @NotBlank(message = "INVALID_PRIVACY")
    @Pattern(regexp = "^PUBLIC|PRIVATE$", message = "INVALID_PRIVACY")
    String privacy;

    String description;

    String tags;

    @NotNull(message = "INVALID_UPLOAD:audioUpload")
    @FileExtension(values = ".mp3, .wav, .m4a", message = "INVALID_FILE_EXTENSION:audioUpload")
    MultipartFile audioUpload;

    @NotNull(message = "INVALID_UPLOAD:imageUpload")
    @FileExtension(values = ".jpeg, .jpg, .png", message = "INVALID_FILE_EXTENSION:imageUpload")
    MultipartFile imageUpload;

    @Min(value = 10, message = "INVALID_DURATION")
    @Max(value = 3600, message = "INVALID_DURATION")
    int duration;

    @Size(min = 1, message = "INVALID_GENRE")
    List<Integer> genreIds;
}
