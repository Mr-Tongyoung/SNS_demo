package com.example.junho.sns_demo.domain.newsFeed.service;

import com.example.junho.sns_demo.domain.post.domain.MediaFile;
import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import com.example.junho.sns_demo.domain.post.repository.PostRepository;
import com.example.junho.sns_demo.global.exception.CustomException;
import com.example.junho.sns_demo.global.exception.ErrorCode;
import com.example.junho.sns_demo.global.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsfeedService {

  private final PostRepository postRepository;
  private final NewsfeedCacheService newsfeedCacheService;

  /**
   * 뉴스피드 불러오기
   * - 캐시 히트: 캐시된 게시글 + DB에서 조회한 인플루언서 게시글 병합 후 정렬하여 반환
   * - 캐시 미스: DB에서 유저가 팔로우한 유저들의 게시글 조회 후 캐싱, 인플루언서 게시글 병합 후 정렬하여 반환
   */
  @Transactional(readOnly = true)  // 트랜잭션을 유지하여 Lazy Loading 허용
  public List<PostResponseDto> getNewsfeed(CustomUserDetails customUserDetails) {
    if(customUserDetails == null){
      throw new CustomException(ErrorCode.LOGIN);
    }
    Long userId = customUserDetails.getId();
    List<Post> allPosts = new ArrayList<>();
    Pageable pageable = PageRequest.of(0, 100);

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
      List<Post> freshPosts = postRepository.findFeedPostsWithFileMediaByUserId(userId, pageable);


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

  @Transactional(readOnly = true)  // 트랜잭션을 유지하여 Lazy Loading 허용
  public List<PostResponseDto> getNewsfeedWithoutJWT(Long userId) {
    List<Post> allPosts = new ArrayList<>();
    Pageable pageable = PageRequest.of(0, 30);

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
      List<Post> freshPosts = postRepository.findFeedPostsWithFileMediaByUserId(userId, pageable);

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


  /**
   * 1️⃣ 캐시 없이, 페치 조인을 사용하지 않고 조회 (N+1 발생)
   */
  @Transactional(readOnly = true)
  public List<PostResponseDto> getNewsfeedWithoutFJ(Long userId) {
    List<Post> allPosts = new ArrayList<>();
    Pageable pageable = PageRequest.of(0, 30);

    System.out.println("***Fetching posts without Fetch Join (N+1 risk)*** for user " + userId);

    // 페치 조인을 사용하지 않고 게시글 조회 (N+1 발생)
    List<Post> freshPosts = postRepository.findFeedPostsByUserIdWithoutFetchJoin(userId, pageable);

//    // Lazy Loading으로 인해 미디어 파일을 조회할 때 추가 쿼리 발생 (N+1)
//    freshPosts.forEach(post -> post.getMediaFiles().toString());
    // 🛠 Lazy Loading 강제 실행 → 명시적으로 미디어 파일을 가져오기 위해 직접 쿼리 실행
    for (Post post : freshPosts) {
      List<MediaFile> mediaFiles = postRepository.findMediaFilesByPostId(post.getId());
      post.setMediaFiles(mediaFiles); // 조회된 데이터를 수동으로 세팅
    }

    allPosts.addAll(freshPosts);

    // 인플루언서 게시글 조회 (이 쿼리는 페치 조인 가능)
    List<Post> influencerPosts = postRepository.findInfluencerPosts(userId);
    allPosts.addAll(influencerPosts);

    return allPosts.stream()
        .sorted(Comparator.comparing(Post::getCreatedAt).reversed()) // 최신순 정렬
        .map(Post::toResponseDto)
        .toList();
  }

  /**
   * 2️⃣ 페치 조인은 유지하지만 캐시 없이 조회
   */
  @Transactional(readOnly = true)
  public List<PostResponseDto> getNewsfeedWithoutCache(Long userId) {
    List<Post> allPosts = new ArrayList<>();
    Pageable pageable = PageRequest.of(0, 30);

    System.out.println("***Fetching posts with Fetch Join but without Cache*** for user " + userId);

    // 페치 조인을 사용하여 N+1 문제 해결, 하지만 캐시는 사용하지 않음
    List<Post> freshPosts = postRepository.findFeedPostsWithFileMediaByUserId(userId, pageable);

    allPosts.addAll(freshPosts);

    // 인플루언서 게시글 조회 (페치 조인 사용 가능)
    List<Post> influencerPosts = postRepository.findInfluencerPosts(userId);
    allPosts.addAll(influencerPosts);

    return allPosts.stream()
        .sorted(Comparator.comparing(Post::getCreatedAt).reversed()) // 최신순 정렬
        .map(Post::toResponseDto)
        .toList();
  }
}
