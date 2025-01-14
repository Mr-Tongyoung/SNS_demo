package com.example.junho.sns_demo.domain.newsFeed.service;

import com.example.junho.sns_demo.domain.post.domain.Post;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class NewsfeedCacheService {

  private static final int MAX_FEED_SIZE = 100; // 최대 저장할 게시글 수
  private static final long CACHE_TTL = 1; // 캐시 TTL (시간 단위)

  @Autowired
  private StringRedisTemplate redisTemplate;

  /**
   * 게시글 ID를 캐시 리스트의 맨 앞에 추가
   */
  public void pushPostToCache(Long userId, Long postId) {
    String key = "userFeed:" + userId;

    // 게시글 ID를 리스트의 맨 앞에 추가
    redisTemplate.opsForList().leftPush(key, postId.toString());

    // 리스트 길이 제한 (오래된 게시글 삭제)
    redisTemplate.opsForList().trim(key, 0, MAX_FEED_SIZE - 1);

    // 캐시에 TTL 설정
    redisTemplate.expire(key, CACHE_TTL, TimeUnit.HOURS);
  }
  /**
   * 게시글 ID를 캐시 리스트의 맨 앞에 추가
   */
  public void addPostToCache(Long userId, Long postId) {
    String key = "userFeed:" + userId;

    // 게시글 ID를 리스트의 맨 앞에 추가
    redisTemplate.opsForList().rightPush(key, postId.toString());

    // 리스트 길이 제한 (오래된 게시글 삭제)
    redisTemplate.opsForList().trim(key, 0, MAX_FEED_SIZE - 1);

    // 캐시에 TTL 설정
    redisTemplate.expire(key, CACHE_TTL, TimeUnit.HOURS);
  }

  /**
   * 특정 유저의 캐시된 게시글 ID 리스트 가져오기
   */
  public List<String> getCachedPosts(Long userId) {
    String key = "userFeed:" + userId;
    return redisTemplate.opsForList().range(key, 0, -1);
  }

  /**
   * 특정 유저의 캐시가 있는지 확인
   */
  public boolean hasCachedPosts(Long userId) {
    String key = "userFeed:" + userId;
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }

  /**
   * 특정 유저의 캐시 삭제
   */
  public void clearUserCache(Long userId) {
    String key = "userFeed:" + userId;
    redisTemplate.delete(key);
  }

  public void removePostFromCache(Long userId, Long postId) {
    String key = "userFeed:" + userId;

    // 캐시에서 특정 게시글 ID 제거
    redisTemplate.opsForList().remove(key, 0, postId.toString());
  }

}
