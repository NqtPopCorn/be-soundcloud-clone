package com.popcorn.soundcloudclone.controller.track;

import com.popcorn.soundcloudclone.domain.dto.ApiResponse;
import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.comment.CommentResponse;
import com.popcorn.soundcloudclone.domain.dto.comment.TrackCommentRequest;
import com.popcorn.soundcloudclone.domain.service.CommentService;
import com.popcorn.soundcloudclone.security.MyUserDetails;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/tracks/{trackId}")
    public ApiResponse<PageResponse<CommentResponse>> getComments(@PathVariable int trackId, Pageable pageable) {
        var comments = commentService.getComments(trackId, pageable);
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .statusCode(200)
                .result(comments)
                .message("Success")
                .build();
    }

    @PostMapping("/tracks/{trackId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<CommentResponse> postComment(@PathVariable int trackId, @RequestBody TrackCommentRequest request,
            @AuthenticationPrincipal MyUserDetails userDetails) {
        int userId = userDetails.getUserId();
        var res = commentService.createComment(trackId, userId, request.getContent());
        return ApiResponse.<CommentResponse>builder()
                .statusCode(200)
                .message("Success")
                .result(res)
                .build();
    }

    @DeleteMapping("/{commId}")
    @PreAuthorize("isAuthenticated()")
    // TODO: check permission
    public ApiResponse deleteComment(@PathVariable long commId, @AuthenticationPrincipal MyUserDetails userDetails) {
        commentService.deleteComment(commId);
        return ApiResponse.builder()
                .statusCode(200)
                .message("Success")
                .build();
    }

}
