package com.example.junho.sns_demo.domain.post.service;

import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.post.dto.PostRequestDto;
import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import com.example.junho.sns_demo.domain.post.repository.PostRepository;
import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.domain.user.repository.UserRepository;
import com.example.junho.sns_demo.global.exception.CustomException;
import com.example.junho.sns_demo.global.exception.ErrorCode;
import lombok.Builder;
import org.springframework.stereotype.Service;

@Service
@Builder
public class PostService {

  private final UserRepository userRepository;
  private final PostRepository postRepository;

  public PostResponseDto createPost(PostRequestDto postRequestDto) {
    User user = validateUserExists(postRequestDto.userId());
    Post post = postRequestDto.toEntity(user);
    return postRepository.save(post).toResponseDto();
  }

  public PostResponseDto getPost(Long id) {
    Post post = validatePostExists(id);
    return post.toResponseDto();
  }

  public PostResponseDto updatePost(Long id, PostRequestDto postRequestDto) {
    Post post = validatePostExists(id);
    validatePostOwnership(post, postRequestDto.userId());

    post.setTitle(postRequestDto.title());
    post.setContent(postRequestDto.content());
    postRepository.save(post);
    return post.toResponseDto();
  }

  public PostResponseDto deletePost(Long id, Long userId) {
    Post post = validatePostExists(id);
    validatePostOwnership(post, userId);
    postRepository.delete(post);
    return post.toResponseDto();
  }


  private User validateUserExists(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
  }

  private Post validatePostExists(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
  }

  private void validatePostOwnership(Post post, Long userId) {
    if (!post.getUser().getId().equals(userId)) {
      throw new CustomException(ErrorCode.POST_NOT_OWNED_BY_USER);
    }
  }
}

