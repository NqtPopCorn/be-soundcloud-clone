package com.popcorn.soundcloudclone.features.comment.service.impl;

import com.popcorn.soundcloudclone.common.response.PageResponse;
import com.popcorn.soundcloudclone.features.comment.dto.response.CommentResponse;
import com.popcorn.soundcloudclone.features.comment.entity.Comment;
import com.popcorn.soundcloudclone.features.track.entity.Track;
import com.popcorn.soundcloudclone.features.comment.repository.CommentRepository;
import com.popcorn.soundcloudclone.features.track.repository.TrackRepository;
import com.popcorn.soundcloudclone.features.users.repository.UserRepository;
import com.popcorn.soundcloudclone.features.comment.service.CommentService;
import com.popcorn.soundcloudclone.common.exception.ApplicationException;
import com.popcorn.soundcloudclone.common.exception.ErrorCode;
import com.popcorn.soundcloudclone.features.comment.mapper.CommentMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TrackRepository trackRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentResponse createComment(int trackId, int userId, String content) {
        try {
            Comment comment = Comment.builder()
                    .author(userRepository.getReferenceById(userId))
                    .track(trackRepository.getReferenceById(trackId))
                    .content(content)
                    .createdAt(LocalDateTime.now())
                    .build();

            return commentMapper.toCommentResponse(commentRepository.save(comment));
        } catch (RuntimeException e) {
            throw new ApplicationException(ErrorCode.NOT_FOUND);
        }
    }

    @Override
    public PageResponse<CommentResponse> getComments(int trackId, Pageable pageable) {
        Specification<Comment> spec = (root, query, cb) -> cb.equal(root.<Track>get("track").<Integer>get("id"),
                trackId);
        return PageResponse.from(commentRepository.findAll(spec, pageable).map(commentMapper::toCommentResponse));
    }

    @Override
    public void deleteComment(long commentId) {
        commentRepository.findById(commentId).orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
        commentRepository.deleteById(commentId);
    }
}
