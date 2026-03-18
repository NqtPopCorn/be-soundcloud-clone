package com.popcorn.soundcloudclone.domain.service;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import com.popcorn.soundcloudclone.domain.dto.comment.CommentResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(int trackId, int userId, String content);

    PageResponse<CommentResponse> getComments(int trackId, Pageable pageable);

    void deleteComment(long commentId);
}
