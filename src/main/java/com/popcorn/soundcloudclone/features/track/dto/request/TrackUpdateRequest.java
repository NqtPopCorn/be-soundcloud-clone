package com.popcorn.soundcloudclone.features.track.dto.request;

import com.popcorn.soundcloudclone.common.validation.FileExtension;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrackUpdateRequest {
    @Size(min = 3, max = 30, message = "INVALID_TRACK_NAME")
    String name;

    @Size(min = 3, max = 30, message = "INVALID_PRIVACY")
    @Pattern(regexp = "^PUBLIC|PRIVATE$", message = "INVALID_PRIVACY")
    String privacy;

    String description;

    String tags;

    @FileExtension(values = ".mp3, .wav, .m4a")
    MultipartFile audioUpload;

    @FileExtension(values = ".jpeg, .png, .jpg")
    MultipartFile imageUpload;

    List<Integer> genreIds = new ArrayList<>();

}
