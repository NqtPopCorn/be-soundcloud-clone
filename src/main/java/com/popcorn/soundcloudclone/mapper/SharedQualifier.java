package com.popcorn.soundcloudclone.mapper;

import com.popcorn.soundcloudclone.domain.entity.FileUpload;
import com.popcorn.soundcloudclone.domain.entity.Track;
import com.popcorn.soundcloudclone.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Setter
public class SharedQualifier {
    /*
     * Chua hoan chinh, thieu cac ham cua track mapper
     */

    @Value("${app.image-base-url}")
    private String imageBaseUrl;

    @Value("${app.audio-base-url}")
    private String audioBaseUrl;

    private final PasswordEncoder passwordEncoder;

    // @Named("getImageUrl")
    // public String getImageUrl(FileUpload fileUpload) {
    // if (fileUpload == null) return null;
    // return imageBaseUrl + fileUpload.getId();
    // }
    //
    // @Named("getAudioUrl")
    // public String getAudioUrl(Track track) {
    // var fileUpload = track.getAudioUpload();
    // if (fileUpload == null) return null;
    // return audioBaseUrl + track.getId() + "/stream";
    // }

    @Named("getRoleName")
    public String getRoleName(User user) {
        return user.getRole().name();
    }

    @Named("hashPassword")
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    // @Named("getUserPlaylists")
    // public Set<PlaylistSummaryResponse> getUserPlaylists(User user) {
    //
    // }
}
