package com.popcorn.soundcloudclone.common.security;

import com.popcorn.soundcloudclone.features.track.entity.Track;
import com.popcorn.soundcloudclone.features.track.repository.TrackRepository;
import com.popcorn.soundcloudclone.features.user.service.FavoriteAlbumService;
import com.popcorn.soundcloudclone.features.user.service.FavoritePlaylistService;
import com.popcorn.soundcloudclone.features.user.service.FavoriteTrackService;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/*
    A context for mapping
*/
@Component
@RequestScope
@RequiredArgsConstructor
public class CurrentUserContext {

    private final FavoritePlaylistService favoritePlaylistService;
    private final FavoriteTrackService favoriteTrackService;
    private final FavoriteAlbumService favoriteAlbumService;
    // private final TrackRepository trackRepository;

    // Cache tạm thời cho request này
    private Set<Integer> likedAlbumIds;
    private Set<Integer> likedTrackIds;
    private Set<Integer> likedPlaylistIds;
    private MyUserDetails userDetails;

    // Hàm lấy UserID từ SecurityContext (Lazy)
    public Integer getCurrentUserId() {
        if (this.userDetails == null) {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof MyUserDetails userDetails) {
                this.userDetails = userDetails;
                return this.userDetails.getUserId();
            } else {
                return null;
            }
        }
        return this.userDetails.getUserId();
    }

    // Lazy Loading
    public boolean isAlbumLiked(Integer albumId) {
        if (getCurrentUserId() == null)
            return false;

        if (this.likedAlbumIds == null) {
            this.likedAlbumIds = favoriteAlbumService.getLikedAlbumIds(this.userDetails.getUserId());
        }
        return this.likedAlbumIds.contains(albumId);
    }

    public boolean isPlaylistLiked(Integer albumId) {
        if (getCurrentUserId() == null)
            return false;

        if (this.likedPlaylistIds == null) {
            this.likedPlaylistIds = favoritePlaylistService.getLikedPlaylistIds(this.userDetails.getUserId());
        }
        return this.likedPlaylistIds.contains(albumId);
    }

    public boolean isTrackLiked(Integer trackId) {
        if (getCurrentUserId() == null)
            return false;

        if (this.likedTrackIds == null) {
            this.likedTrackIds = favoriteTrackService.getLikedTrackIds(this.userDetails.getUserId());
        }
        return this.likedTrackIds.contains(trackId);
    }

    // public boolean hasTrackPermit(int trackId) {
    // if (getCurrentUserId() == null) return false;
    // Track track = trackRepository.findByIdAndArtistId(trackId,
    // userDetails.getUserId()).orElse(null);
    // if (track == null) {
    // return false;
    // }
    // boolean isTrackPublic = track.getPrivacy() == Track.Privacy.PUBLIC;
    // return isTrackPublic || isAdmin();
    // }
    //
    // private boolean isAdmin() {
    // if (this.userDetails == null) {
    // return false;
    // }
    // return userDetails.getAuthorities().stream()
    // .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    // }
}