package com.example.junho.sns_demo.domain.post.dto;

import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.user.domain.User;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


public record PostResponseDto(
    String title,
    String content,
    int likes,
    String username,
    Long userId,
    List<String> mediaUrls,
    LocalDateTime createdDate
) implements Serializable {
}
