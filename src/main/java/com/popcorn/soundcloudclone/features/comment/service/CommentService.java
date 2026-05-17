package com.popcorn.soundcloudclone.features.comment.service;

import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.comment.dto.response.CommentResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(int trackId, int userId, String content);

    PageResponse<CommentResponse> getComments(int trackId, Pageable pageable);

    void deleteComment(long commentId);
}
