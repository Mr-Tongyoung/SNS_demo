package com.example.junho.sns_demo.domain.newsFeed.service;

import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import com.example.junho.sns_demo.domain.post.repository.PostRepository;
import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.domain.user.repository.UserRepository;
import com.example.junho.sns_demo.global.jwt.CustomUserDetails;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsfeedService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final NewsfeedCacheService newsfeedCacheService;

  /**
   * 뉴스피드 불러오기
   */
  public List<PostResponseDto> getNewsfeed(CustomUserDetails customUserDetails) {
    User user = userRepository.findByUsername(customUserDetails.getUsername());
    customUserDetails.getUser().setId(user.getId());
    Long userId = customUserDetails.getId();

    if (newsfeedCacheService.hasCachedPosts(userId)) {
      System.out.println("***Cache hit for user*** " + userId);
      // 캐시에서 정렬된 ID 리스트 가져오기
      List<String> cachedPostIds = newsfeedCacheService.getCachedPosts(userId);

      // DB에서 모든 Post 가져오기
      List<Post> posts = postRepository.findAllById(
          cachedPostIds.stream().map(Long::valueOf).toList()
      );

      // 캐시에 저장된 ID 순서대로 정렬
      Map<Long, Post> postMap = posts.stream()
          .collect(Collectors.toMap(Post::getId, post -> post));

      List<PostResponseDto> responseDtos = cachedPostIds.stream()
          .map(Long::valueOf)
          .map(postMap::get) // 캐시 순서대로 정렬
          .filter(Objects::nonNull) // 존재하지 않는 ID 필터링
          .map(Post::toResponseDto)
          .toList();

      return responseDtos;
    } else {
      // 캐시에 데이터가 없으면 DB에서 조회 후 캐시에 저장
      System.out.println("***Cache miss for user*** " + userId);

      List<Post> posts = postRepository.findFeedPostsWithFileMediaByUserId(userId);
      posts.forEach(post -> newsfeedCacheService.addPostToCache(userId, post.getId()));

      return posts.stream()
          .map(Post::toResponseDto)
          .toList();
    }
  }
}