package com.example.junho.sns_demo.domain.like.dto;

import lombok.Builder;

@Builder
public record LikeResponseDto(
    Long postId,
    Long userId,
    int likeCount,
    boolean liked
) {

}
