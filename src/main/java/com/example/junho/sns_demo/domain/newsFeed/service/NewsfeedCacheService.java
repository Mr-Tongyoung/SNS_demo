package com.example.junho.sns_demo.domain.newsFeed.service;

import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsfeedCacheService {

  private static final long CACHE_TTL = 6; // in hours

  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  /**
   * 직렬화된 뉴스피드(PostResponseDto 리스트) 저장
   */
  public void cacheNewsfeed(Long userId, List<PostResponseDto> posts) {
    String key = "newsfeed:" + userId;
    try {
      String json = objectMapper.writeValueAsString(posts);
      redisTemplate.opsForValue().set(key, json, Duration.ofHours(CACHE_TTL));
    } catch (JsonProcessingException e) {
      log.error("❌ Redis 저장 실패: {}", e.getMessage(), e);
    }
  }

  /**
   * Redis에서 캐시된 뉴스피드 가져오기
   */
  public List<PostResponseDto> getCachedNewsfeed(Long userId) {
    String key = "newsfeed:" + userId;
    String json = redisTemplate.opsForValue().get(key);
    if (json == null) return null;

    try {
      return objectMapper.readValue(json, new TypeReference<>() {});
    } catch (JsonProcessingException e) {
      log.error("❌ Redis 역직렬화 실패: {}", e.getMessage(), e);
      return null;
    }
  }
}
