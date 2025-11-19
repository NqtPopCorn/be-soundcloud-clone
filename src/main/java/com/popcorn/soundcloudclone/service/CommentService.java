package com.popcorn.soundcloudclone.service;

import com.popcorn.soundcloudclone.domain.dto.comment.CommentRequest;
import com.popcorn.soundcloudclone.domain.dto.comment.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(CommentRequest commentRequest);
    CommentResponse getComment(Long commentId);

    List<CommentResponse> getComments(int trackId);
    CommentResponse updateComment(CommentRequest commentRequest);
    void deleteComment(Long commentId);
}
