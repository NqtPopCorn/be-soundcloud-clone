package com.popcorn.soundcloudclone.common.utils;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.popcorn.soundcloudclone.features.users.entity.User;

@Component
@RequiredArgsConstructor
@Setter
public class SharedQualifier {
    private final PasswordEncoder passwordEncoder;

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
