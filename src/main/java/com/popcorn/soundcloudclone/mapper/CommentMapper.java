package com.popcorn.soundcloudclone.mapper;

import com.popcorn.soundcloudclone.domain.dto.comment.CommentResponse;
import com.popcorn.soundcloudclone.domain.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface CommentMapper {

    @Mapping(target = "commentId", source = "id")
    CommentResponse toCommentResponse(Comment comment);

}
