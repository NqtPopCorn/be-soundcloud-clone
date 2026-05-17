package com.popcorn.soundcloudclone.features.comment.dto.response;

import java.time.LocalDateTime;

import com.popcorn.soundcloudclone.features.users.dto.response.UserSummaryResponse;

import lombok.Data;

@Data
public class CommentResponse {
    Long commentId;
    String content;
    UserSummaryResponse author;
    LocalDateTime createdAt;
}
