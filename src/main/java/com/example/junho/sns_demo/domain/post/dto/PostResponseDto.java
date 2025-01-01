package com.example.junho.sns_demo.domain.post.dto;

import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.user.domain.User;
import java.time.LocalDateTime;


public record PostResponseDto(
    String title,
    String content,
    String userName,
    Long userId,
    LocalDateTime createdDate
) {

}
