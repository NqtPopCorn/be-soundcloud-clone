package com.popcorn.soundcloudclone.common.aop;

import com.popcorn.soundcloudclone.common.security.CurrentUserContext;
import com.popcorn.soundcloudclone.features.track.dto.response.TrackResponse;
import com.popcorn.soundcloudclone.features.album.dto.response.AlbumResponse;
import com.popcorn.soundcloudclone.features.album.dto.response.AlbumSummaryResponse;
import com.popcorn.soundcloudclone.features.playlist.dto.response.PlaylistResponse;
import com.popcorn.soundcloudclone.features.playlist.dto.response.PlaylistSummaryResponse;
import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Aspect
@Component
@RequiredArgsConstructor
public class InjectLikedAspect {

    private final CurrentUserContext currentUserContext;

    @AfterReturning(pointcut = "execution(* com.popcorn.soundcloudclone.features..controller..*(..))", returning = "result")
    public void injectLikedStatus(Object result) {
        if (currentUserContext.getCurrentUserId() == null) {
            return;
        }
        processObject(result);
    }

    private void processObject(Object obj) {
        if (obj == null) return;

        if (obj instanceof ResponseEntity<?> responseEntity) {
            processObject(responseEntity.getBody());
        } else if (obj instanceof ApiResponse<?> apiResponse) {
            processObject(apiResponse.result);
        } else if (obj instanceof PageResponse<?> pageResponse) {
            processCollection(pageResponse.getItems());
        } else if (obj instanceof Collection<?> collection) {
            processCollection(collection);
        } else if (obj instanceof TrackResponse track) {
            track.setLiked(currentUserContext.isTrackLiked(track.getId()));
        } else if (obj instanceof AlbumResponse album) {
            album.setLiked(currentUserContext.isAlbumLiked(album.getId()));
            if (album.getTracks() != null) {
                processCollection(album.getTracks());
            }
        } else if (obj instanceof AlbumSummaryResponse albumSummary) {
            albumSummary.setLiked(currentUserContext.isAlbumLiked(albumSummary.getId()));
        } else if (obj instanceof PlaylistResponse playlist) {
            playlist.setLiked(currentUserContext.isPlaylistLiked(playlist.getId()));
            if (playlist.getTracks() != null) {
                processCollection(playlist.getTracks());
            }
        } else if (obj instanceof PlaylistSummaryResponse playlistSummary) {
            playlistSummary.setLiked(currentUserContext.isPlaylistLiked(playlistSummary.getId()));
        }
    }

    private void processCollection(Collection<?> collection) {
        if (collection == null) return;
        for (Object item : collection) {
            processObject(item);
        }
    }
}
