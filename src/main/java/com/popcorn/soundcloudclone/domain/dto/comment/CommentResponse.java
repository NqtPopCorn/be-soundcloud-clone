package com.popcorn.soundcloudclone.domain.dto.comment;

import java.util.List;

public class CommentResponse {
    Long commentId;
    String content;
    Long parentId;
    int userId;

    List<CommentResponse> children;
}
