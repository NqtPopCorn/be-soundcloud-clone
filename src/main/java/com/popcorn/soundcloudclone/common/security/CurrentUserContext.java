package com.popcorn.soundcloudclone.common.security;

import com.popcorn.soundcloudclone.features.user.service.FavoriteAlbumService;
import com.popcorn.soundcloudclone.features.user.service.FavoritePlaylistService;
import com.popcorn.soundcloudclone.features.user.service.FavoriteTrackService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@RequiredArgsConstructor
public class CurrentUserContext {

    private final FavoritePlaylistService favoritePlaylistService;
    private final FavoriteTrackService favoriteTrackService;
    private final FavoriteAlbumService favoriteAlbumService;

    private MyUserDetails userDetails;

    public Integer getCurrentUserId() {
        if (this.userDetails == null) {
            var auth = SecurityContextHolder
                    .getContext()
                    .getAuthentication();

            if (auth != null
                    && auth.getPrincipal() instanceof MyUserDetails ud) {
                this.userDetails = ud;
            } else {
                return null;
            }
        }

        return this.userDetails.getUserId();
    }

    public boolean isAlbumLiked(Integer albumId) {
        Integer userId = getCurrentUserId();

        if (userId == null) {
            return false;
        }

        return favoriteAlbumService
                .getLikedAlbumIds(userId)
                .contains(albumId);
    }

    public boolean isPlaylistLiked(Integer playlistId) {
        Integer userId = getCurrentUserId();

        if (userId == null) {
            return false;
        }

        return favoritePlaylistService
                .getLikedPlaylistIds(userId)
                .contains(playlistId);
    }

    public boolean isTrackLiked(Integer trackId) {
        Integer userId = getCurrentUserId();

        if (userId == null) {
            return false;
        }

        return favoriteTrackService
                .getLikedTrackIds(userId)
                .contains(trackId);
    }
}