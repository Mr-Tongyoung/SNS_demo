package com.example.junho.sns_demo.domain.newsFeed.service;

import com.example.junho.sns_demo.domain.post.domain.MediaFile;
import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import com.example.junho.sns_demo.domain.post.repository.PostRepository;
import com.example.junho.sns_demo.domain.user.repository.FollowRepository;
import com.example.junho.sns_demo.global.exception.CustomException;
import com.example.junho.sns_demo.global.exception.ErrorCode;
import com.example.junho.sns_demo.global.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsfeedService {

  private final PostRepository postRepository;
  private final FollowRepository followRepository;
  private final NewsfeedCacheService newsfeedCacheService;

  @Transactional(readOnly = true)
  public List<PostResponseDto> getNewsfeed(
      CustomUserDetails customUserDetails) {
    if (customUserDetails == null) {
      throw new CustomException(ErrorCode.LOGIN);
    }
    return getNewsfeedNoJWT(customUserDetails.getId());
  }

  @Transactional(readOnly = true)
  public List<PostResponseDto> getNewsfeedNoJWT(Long userId) {
    List<PostResponseDto> allPosts;
    Pageable pageable = PageRequest.of(0, 30);

    List<PostResponseDto> cached = newsfeedCacheService.getCachedNewsfeed(
        userId);
    if (cached != null) {
      System.out.println("***Cache hit for user*** " + userId);
      allPosts = new ArrayList<>(cached);
    } else {
      System.out.println("***Cache miss for user*** " + userId);

      // 1. User + Post Fetch Join
      List<Post> freshPosts = postRepository.findPostsWithUserAndFilterByFollow(
          userId, pageable);

      // 2. MediaFile 일괄 조회
      List<Long> postIds = freshPosts.stream()
          .map(Post::getId)
          .toList();
      List<MediaFile> mediaFiles = postRepository.findMediaFilesByPostIds(
          postIds);

      // 3. PostId 기준으로 그룹핑
      Map<Long, List<MediaFile>> mediaMap = mediaFiles.stream()
          .collect(Collectors.groupingBy(m -> m.getPost().getId()));

      // 4. Post에 MediaFile 수동 주입
      for (Post post : freshPosts) {
        post.setMediaFiles(mediaMap.getOrDefault(post.getId(), List.of()));
      }

      // 5. DTO 변환
      allPosts = freshPosts.stream()
          .map(Post::toResponseDto)
          .collect(Collectors.toList());

      // 6. 캐시 저장
      newsfeedCacheService.cacheNewsfeed(userId, allPosts);
    }

    // 7. 인플루언서 포스트 추가
    List<Post> influencerPosts = postRepository.findInfluencerPosts(userId);
    List<PostResponseDto> influencerDtos = influencerPosts.stream()
        .map(Post::toResponseDto)
        .toList();

    allPosts.addAll(influencerDtos);

    // 8. 최종 정렬
    return allPosts.stream()
        .sorted(Comparator.comparing(PostResponseDto::createdDate).reversed())
        .toList();
  }

  @Transactional(readOnly = true)
  public List<PostResponseDto> getNewsfeedNoCache(Long userId) {
    Pageable pageable = PageRequest.of(0, 30);
    System.out.println("***Fetching posts with Fetch Join (optimized) but without Cache*** for user " + userId);

    // 1. User + Post Fetch Join
    List<Post> freshPosts = postRepository.findPostsWithUserAndFilterByFollow(userId, pageable);

    // 2. Post ID 수집 → MediaFile IN 조회
    List<Long> postIds = freshPosts.stream()
        .map(Post::getId)
        .toList();

    // 3. MediaFile 일괄 조회
    List<MediaFile> mediaFiles = postRepository.findMediaFilesByPostIds(postIds);

    // 4. MediaFile → PostId 기준으로 그룹핑
    Map<Long, List<MediaFile>> mediaMap = mediaFiles.stream()
        .collect(Collectors.groupingBy(m -> m.getPost().getId()));

    // 5. Post에 MediaFile 수동 주입
    for (Post post : freshPosts) {
      post.setMediaFiles(mediaMap.getOrDefault(post.getId(), List.of()));
    }

    // 6. Post → Dto 변환
    List<PostResponseDto> allPosts = freshPosts.stream()
        .map(Post::toResponseDto)
        .collect(Collectors.toList());

    // 7. 인플루언서 포스트 추가
    List<Post> influencerPosts = postRepository.findInfluencerPosts(userId);
    List<PostResponseDto> influencerDtos = influencerPosts.stream()
        .map(Post::toResponseDto)
        .toList();

    allPosts.addAll(influencerDtos);

    // 8. 최종 정렬
    return allPosts.stream()
        .sorted(Comparator.comparing(PostResponseDto::createdDate).reversed())
        .toList();
  }

  @Transactional(readOnly = true)
  public List<PostResponseDto> getNewsfeedNoCacheNoFJ(Long userId) {
    Pageable pageable = PageRequest.of(0, 30);
    System.out.println(
        "***Fetching posts without Fetch Join (step-by-step, N+1)*** for user "
            + userId);

    // 1. 팔로우한 유저 ID들 조회
    List<Long> followingIds = followRepository.findFollowingIdsByUserId(userId);
    if (followingIds.isEmpty()) {
      return List.of();
    }

    // 2. 팔로우한 유저들의 포스트 가져오기
    List<Post> freshPosts = new ArrayList<>();
    for (Long followingId : followingIds) {
      freshPosts.addAll(
          postRepository.findPostsByUserId(followingId, pageable));
    }

    // 3. 각 포스트에 대해 미디어파일 개별 조회
    for (Post post : freshPosts) {
      List<MediaFile> mediaFiles = postRepository.findMediaFilesByPostId(
          post.getId());
      post.setMediaFiles(mediaFiles);  // setter로 N+1 유도
    }

    // 4. DTO로 변환
    List<PostResponseDto> allPosts = freshPosts.stream()
        .map(Post::toResponseDto)
        .collect(Collectors.toList());

    // 5. 인플루언서 포스트 추가
    List<Post> influencerPosts = postRepository.findInfluencerPosts(userId);
    List<PostResponseDto> influencerDtos = influencerPosts.stream()
        .map(Post::toResponseDto)
        .toList();

    allPosts.addAll(influencerDtos);

    // 6. 정렬 후 상위 30개 리턴
    return allPosts.stream()
        .sorted(Comparator.comparing(PostResponseDto::createdDate).reversed())
        .limit(30)
        .toList();
  }


}
