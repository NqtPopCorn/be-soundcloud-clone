package com.popcorn.soundcloudclone.security;

import com.popcorn.soundcloudclone.domain.dto.track.TrackResponse;
import com.popcorn.soundcloudclone.service.AlbumService;
import com.popcorn.soundcloudclone.service.PlaylistService;
import com.popcorn.soundcloudclone.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("userSecurity")
@RequiredArgsConstructor
/*
* Bean kiem tra quyen cua user
* */
public class UserSecurity {

    private final TrackService trackService;
    private final AlbumService albumService;
    private final PlaylistService playlistService;

    public boolean isTrackOwnerOrAdmin(int trackId, Authentication auth) {
        try {
            UserDetails user = (UserDetails) auth.getPrincipal();
            TrackResponse track = trackService.getTrackResponse(trackId);
            if (track == null) return false;

            return isAdmin() || Objects.equals(track.getArtist().getUsername(), user.getUsername());
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }


}

