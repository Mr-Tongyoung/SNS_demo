package com.example.junho.sns_demo.domain.post.dto;

import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.user.domain.User;

public record PostRequestDto(
    String title,
    String content,
    Long userId

){
  public Post toEntity(User user) {
    return Post.builder()
        .title(this.title)
        .content(this.content)
        .user(user)
        .build();
  }

}
