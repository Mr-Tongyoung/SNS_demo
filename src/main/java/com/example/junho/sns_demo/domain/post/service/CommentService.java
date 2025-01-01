package com.example.junho.sns_demo.domain.post.service;

import com.example.junho.sns_demo.domain.post.domain.Comment;
import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.post.dto.CommentRequestDto;
import com.example.junho.sns_demo.domain.post.dto.CommentResponseDto;
import com.example.junho.sns_demo.domain.post.repository.CommentRepository;
import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.global.util.ValidationService;
import java.util.List;
import lombok.Builder;
import org.springframework.stereotype.Service;

@Service
@Builder
public class CommentService {

  private final ValidationService validationService;
  private final CommentRepository commentRepository;

  public CommentResponseDto createComment(CommentRequestDto commentRequestDto) {
    User user = validationService.validateUser(commentRequestDto.userId());
    Post post = validationService.validatePost(commentRequestDto.postId());
    Comment comment = commentRequestDto.toEntity(user, post);

    return commentRepository.save(comment).toResponseDto();
  }

  public List<CommentResponseDto> getComments(Long postId) {
    validationService.validatePost(postId);

    List<Comment> comments = commentRepository.findAllByPostId(postId);
    return comments.stream()
        .map(Comment::toResponseDto)
        .toList();
  }

  public CommentResponseDto updateComment(Long commentId, CommentRequestDto commentRequestDto) {
    Comment comment = validationService.validateComment(commentId);
    validationService.validateCommentOwnership(comment, commentRequestDto.userId());

    comment.setContent(commentRequestDto.content());
    commentRepository.save(comment);

    return comment.toResponseDto();
  }

  public void deleteComment(Long commentId, Long userId) {
    Comment comment = validationService.validateComment(commentId);
    validationService.validateCommentOwnership(comment, userId);

    commentRepository.deleteById(commentId);
  }
}

