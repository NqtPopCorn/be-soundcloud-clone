package com.popcorn.soundcloudclone.features.comment.mapper;

import com.popcorn.soundcloudclone.features.comment.dto.response.CommentResponse;
import com.popcorn.soundcloudclone.features.comment.entity.Comment;
import com.popcorn.soundcloudclone.features.users.mapper.UserMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface CommentMapper {

    @Mapping(target = "commentId", source = "id")
    CommentResponse toCommentResponse(Comment comment);

}
