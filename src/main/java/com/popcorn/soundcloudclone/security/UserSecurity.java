package com.popcorn.soundcloudclone.security;

import com.popcorn.soundcloudclone.domain.dto.track.TrackResponse;
import com.popcorn.soundcloudclone.domain.entity.Track;
import com.popcorn.soundcloudclone.service.AlbumService;
import com.popcorn.soundcloudclone.service.PlaylistService;
import com.popcorn.soundcloudclone.service.TrackServiceV2;
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

    private final TrackServiceV2 trackService;
    private final AlbumService albumService;
    private final PlaylistService playlistService;

    public boolean hasTrackPermit(int trackId, UserDetails userDetails) {
        TrackResponse track = trackService.getTrackResponse(trackId);
        if(track == null) return false;
        boolean isOwner = userDetails != null && Objects.equals(track.getArtist().getUsername(), userDetails.getUsername());
        boolean isTrackPublic = track.getPrivacy().equals(Track.Privacy.PRIVATE.name());
        return isAdmin() || isOwner || isTrackPublic;
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }


}

