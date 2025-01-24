package com.example.junho.sns_demo.domain.like.dto;

import com.example.junho.sns_demo.domain.like.domain.Like;
import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.user.domain.User;

public record LikeRequestDto(
    Long postId
) {

  public Like toEntity(Post post, User user) {
    return Like.builder()
        .post(post)
        .user(user)
        .build();
  }

}
