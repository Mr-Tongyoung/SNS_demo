package com.example.junho.sns_demo.domain.post.service;

import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PostCacheService {

  private final RedisTemplate<String, Object> redisTemplate;

  // 게시글 저장
  public void savePost(Long postId, PostResponseDto postDto) {
    String key = "post:" + postId;
    redisTemplate.opsForValue().set(key, postDto, 10, TimeUnit.MINUTES); // TTL 10분
  }

  // 게시글 조회
  public PostResponseDto getPost(Long postId) {
    String key = "post:" + postId;
    return (PostResponseDto) redisTemplate.opsForValue().get(key);
  }
}
