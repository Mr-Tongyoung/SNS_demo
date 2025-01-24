package com.example.junho.sns_demo.domain.comment.service;

import com.example.junho.sns_demo.domain.comment.domain.Comment;
import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.comment.dto.CommentRequestDto;
import com.example.junho.sns_demo.domain.comment.dto.CommentResponseDto;
import com.example.junho.sns_demo.domain.comment.repository.CommentRepository;
import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.global.jwt.CustomUserDetails;
import com.example.junho.sns_demo.global.util.ValidationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final ValidationService validationService;
  private final CommentRepository commentRepository;

  public CommentResponseDto createComment(CommentRequestDto commentRequestDto
      , CustomUserDetails customUserDetails) {
    User user = validationService.validateUser(customUserDetails.getId());
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

  public CommentResponseDto updateComment(Long commentId,
      CommentRequestDto commentRequestDto,
      CustomUserDetails customUserDetails) {
    Comment comment = validationService.validateComment(commentId);
    validationService.validateCommentOwnership(comment,
        customUserDetails.getId());

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

