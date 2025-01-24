package com.example.junho.sns_demo.domain.comment.dto;

import java.time.LocalDateTime;

public record CommentResponseDto(
    String content,
    String userName,
    LocalDateTime createdAt
) {

}
