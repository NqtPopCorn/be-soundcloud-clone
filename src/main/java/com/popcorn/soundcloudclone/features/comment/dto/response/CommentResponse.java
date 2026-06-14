package com.popcorn.soundcloudclone.features.comment.dto.response;

import java.time.LocalDateTime;

import com.popcorn.soundcloudclone.features.users.dto.response.UserSummaryResponse;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    Long commentId;
    String content;
    UserSummaryResponse author;
    LocalDateTime createdAt;
}

