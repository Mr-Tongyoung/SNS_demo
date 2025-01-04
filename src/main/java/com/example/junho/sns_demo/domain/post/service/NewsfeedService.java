package com.example.junho.sns_demo.domain.post.service;

import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import com.example.junho.sns_demo.domain.post.repository.PostRepository;
import com.example.junho.sns_demo.domain.user.repository.FollowRepository;
import com.example.junho.sns_demo.domain.user.repository.UserRepository;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsfeedService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final FollowRepository followRepository;


  public List<PostResponseDto> getNewsfeed(Long userId) {
    // 1. 팔로우한 유저 ID 가져오기
    List<Long> followingIds = followRepository.findFollowingIdsByFollowerId(
        userId);

    // 2. 팔로우한 유저들의 게시글 가져오기
    List<Post> posts = postRepository.findPostsByUserIdsOrderByCreatedAtDesc(
        followingIds);

    // 3. 게시글을 DTO로 변환
    return posts.stream()
        .map(Post::toResponseDto)
        .toList();
  }
}
