package com.example.junho.sns_demo.domain.newsFeed.service;

import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import com.example.junho.sns_demo.domain.post.repository.PostRepository;
import com.example.junho.sns_demo.global.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsfeedService {

  private final PostRepository postRepository;
  private final NewsfeedCacheService newsfeedCacheService;

  /**
   * 뉴스피드 불러오기
   * - 캐시 히트: 캐시된 게시글 + DB에서 조회한 인플루언서 게시글 병합 후 정렬하여 반환
   * - 캐시 미스: DB에서 유저가 팔로우한 유저들의 게시글 조회 후 캐싱, 인플루언서 게시글 병합 후 정렬하여 반환
   */
  public List<PostResponseDto> getNewsfeed(CustomUserDetails customUserDetails) {
    Long userId = customUserDetails.getId();
    List<Post> allPosts = new ArrayList<>();

    if (newsfeedCacheService.hasCachedPosts(userId)) {
      System.out.println("***Cache hit for user*** " + userId);

      // 캐시에서 유저의 뉴스피드 게시글 ID 리스트 가져오기
      List<String> cachedPostIds = newsfeedCacheService.getCachedPosts(userId);

      // 캐시에 저장된 게시글 ID를 기반으로 DB에서 조회
      List<Post> cachedPosts = postRepository.findAllById(
          cachedPostIds.stream().map(Long::valueOf).toList()
      );

      // 캐시에 저장된 ID 순서대로 정렬
      Map<Long, Post> postMap = cachedPosts.stream()
          .collect(Collectors.toMap(Post::getId, post -> post));

      List<Post> sortedCachedPosts = cachedPostIds.stream()
          .map(Long::valueOf)
          .map(postMap::get) // 캐시 순서대로 정렬
          .filter(Objects::nonNull) // 존재하지 않는 ID 필터링
          .toList();

      allPosts.addAll(sortedCachedPosts);
    } else {
      System.out.println("***Cache miss for user*** " + userId);

      // 캐시에 없는 경우 DB에서 유저가 팔로우한 유저들의 최신 피드 조회 후 캐시에 저장
      List<Post> freshPosts = postRepository.findFeedPostsWithFileMediaByUserId(userId);

      // 캐시에 저장
      freshPosts.forEach(post -> newsfeedCacheService.addPostToCache(userId, post.getId()));

      allPosts.addAll(freshPosts);
    }

    // 인플루언서 게시글을 따로 조회
    List<Post> influencerPosts = postRepository.findInfluencerPosts(userId);
    allPosts.addAll(influencerPosts);

    // 최신 게시글 순으로 정렬 후 반환
    return allPosts.stream()
        .sorted(Comparator.comparing(Post::getCreatedAt).reversed()) // 최신순 정렬
        .map(Post::toResponseDto)
        .toList();
  }
}
