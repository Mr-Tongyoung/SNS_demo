package com.example.junho.sns_demo.domain.newsFeed.service;

import com.example.junho.sns_demo.domain.post.domain.MediaFile;
import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import com.example.junho.sns_demo.domain.post.repository.PostRepository;
import com.example.junho.sns_demo.domain.user.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsfeedUpdateService {

  private final NewsfeedCacheService newsfeedCacheService;
  private final FollowRepository followRepository;
  private final PostRepository postRepository;

  /**
   * 게시글 작성 시 팔로워 캐시 업데이트
   */
  @Transactional
  public void updateFollowerCaches(Long userId, Long postId) {
    List<Long> followerIds = followRepository.findFollowers(userId);

    Optional<Post> optionalPost = postRepository.findWithMediaFilesById(postId);

    if (optionalPost.isEmpty()) {
      log.error("Post ID {} DB에서 찾을 수 없음. 캐시 갱신 중단", postId);
      return;
    }

    Post post = optionalPost.get();
    PostResponseDto dto = post.toResponseDto();

    // 병렬 처리로 성능 향상
    followerIds.parallelStream().forEach(followerId -> {
      try {
        List<PostResponseDto> cached = newsfeedCacheService.getCachedNewsfeed(followerId);
        if (cached == null) return;

        List<PostResponseDto> updated = new ArrayList<>(cached);
        updated.add(0, dto);
        if (updated.size() > 100) {
          updated = updated.subList(0, 100);
        }
        newsfeedCacheService.cacheNewsfeed(followerId, updated);
      } catch (Exception e) {
        log.error("캐시 업데이트 실패: followerId={}, postId={}", followerId, postId, e);
      }
    });
  }
}
