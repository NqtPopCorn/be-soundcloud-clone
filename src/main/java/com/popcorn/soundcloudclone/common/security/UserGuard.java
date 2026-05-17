package com.popcorn.soundcloudclone.common.security;

import com.popcorn.soundcloudclone.features.track.dto.response.TrackResponse;
import com.popcorn.soundcloudclone.features.track.entity.Track;
import com.popcorn.soundcloudclone.features.comment.repository.CommentRepository;
import com.popcorn.soundcloudclone.features.track.service.TrackService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("userGuard")
@RequiredArgsConstructor
/*
 * Bean kiem tra quyen cua user, dung chung cho tat ca request
 */
public class UserGuard {

    private final TrackService trackService;
    private final CommentRepository commentRepository;

    public boolean hasTrackPermit(int trackId, MyUserDetails userDetails) {
        TrackResponse track = trackService.getTrackResponse(trackId);
        if (track == null)
            return false;
        boolean isOwner = userDetails != null
                && Objects.equals(track.getArtist().getUsername(), userDetails.getUsername());
        boolean isTrackPublic = track.getPrivacy() == Track.Privacy.PUBLIC;
        return isAdmin() || isOwner || isTrackPublic;
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return false;
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean hasCommentPermit(Long commentId, MyUserDetails userDetails) {
        var comment = commentRepository.findById(commentId);
        if (comment.isEmpty())
            return false;
        boolean isOwner = userDetails != null
                && Objects.equals(comment.get().getAuthor().getId(), userDetails.getUserId());
        return isAdmin() || isOwner;
    }

}
