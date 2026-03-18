package com.popcorn.soundcloudclone.domain.dto.comment;

import java.time.LocalDateTime;

import com.popcorn.soundcloudclone.domain.dto.user.UserSummaryResponse;
import lombok.Data;

@Data
public class CommentResponse {
    Long commentId;
    String content;
    UserSummaryResponse author;
    LocalDateTime createdAt;
}
