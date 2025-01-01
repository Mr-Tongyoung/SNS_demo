package com.example.junho.sns_demo.domain.post.dto;

import java.time.LocalDateTime;

public record CommentResponseDto(
    String content,
    String userName,
    LocalDateTime createdAt
) {

}
