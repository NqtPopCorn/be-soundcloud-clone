package com.popcorn.soundcloudclone.domain.dto.comment;

import com.popcorn.soundcloudclone.domain.dto.user.UserSummaryResponse;

import java.util.List;

public class CommentResponse {
    Long commentId;
    String content;
    UserSummaryResponse author;
}
