package com.example.junho.sns_demo.domain.comment.dto;

import com.example.junho.sns_demo.domain.comment.domain.Comment;
import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.user.domain.User;

public record CommentRequestDto(
    String content,
    Long postId
) {
  public Comment toEntity(User user, Post post){
    return Comment.builder()
        .content(content)
        .user(user)
        .post(post)
        .build();
  }

}
