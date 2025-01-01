package com.example.junho.sns_demo.global.util;

import com.example.junho.sns_demo.domain.post.domain.Comment;
import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.post.repository.CommentRepository;
import com.example.junho.sns_demo.domain.post.repository.PostRepository;
import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.domain.user.repository.UserRepository;
import com.example.junho.sns_demo.global.exception.CustomException;
import com.example.junho.sns_demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidationService {

  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  public User validateUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
  }

  public Post validatePost(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
  }

  public Comment validateComment(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
  }

  public void validatePostOwnership(Post post, Long userId) {
    if (!post.getUser().getId().equals(userId)) {
      throw new CustomException(ErrorCode.POST_NOT_OWNED_BY_USER);
    }
  }

  public void validateCommentOwnership(Comment comment, Long userId) {
    if (!comment.getUser().getId().equals(userId)) {
      throw new CustomException(ErrorCode.COMMENT_NOT_OWNED_BY_USER);
    }
  }
}
