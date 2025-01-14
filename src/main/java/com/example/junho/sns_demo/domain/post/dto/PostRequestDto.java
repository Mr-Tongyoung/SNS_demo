package com.example.junho.sns_demo.domain.post.dto;

import com.example.junho.sns_demo.domain.post.domain.MediaFile;
import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.user.domain.User;
import java.io.Serializable;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record PostRequestDto(
    String title,
    String content
) implements Serializable {
  public Post toEntity(User user) {
    return Post.builder()
        .title(this.title)
        .content(this.content)
        .user(user)
        .likes(0)
        .build();
  }

}
