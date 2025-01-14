package com.example.junho.sns_demo.domain.newsFeed.service;

import com.example.junho.sns_demo.domain.user.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsfeedUpdateService {

  private final NewsfeedCacheService newsfeedCacheService;
  private final FollowRepository followRepository;

  /**
   * 게시글 작성 시 팔로워 캐시 업데이트
   */
  public void updateFollowerCaches(Long userId, Long postId) {
    // 팔로워 ID 리스트 가져오기
    List<Long> followerIds = followRepository.findFollowers(userId);
    System.out.println(userId + "'s follwerId: " + followerIds);

    // 각 팔로워의 캐시에 게시글 ID 추가
    for (Long followerId : followerIds) {
      newsfeedCacheService.pushPostToCache(followerId, postId);
    }
  }
}
