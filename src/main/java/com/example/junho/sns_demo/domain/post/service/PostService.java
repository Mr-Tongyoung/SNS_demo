package com.example.junho.sns_demo.domain.post.service;

import com.example.junho.sns_demo.domain.post.domain.Comment;
import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.post.dto.CommentResponseDto;
import com.example.junho.sns_demo.domain.post.dto.PostRequestDto;
import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import com.example.junho.sns_demo.domain.post.repository.CommentRepository;
import com.example.junho.sns_demo.domain.post.repository.PostRepository;
import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.domain.user.repository.UserRepository;
import com.example.junho.sns_demo.global.exception.CustomException;
import com.example.junho.sns_demo.global.exception.ErrorCode;
import com.example.junho.sns_demo.global.util.ValidationService;
import java.util.List;
import lombok.Builder;
import org.springframework.stereotype.Service;

@Service
@Builder
public class PostService {

  private final ValidationService validationService;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  public PostResponseDto createPost(PostRequestDto postRequestDto) {
    User user = validationService.validateUser(postRequestDto.userId());
    Post post = postRequestDto.toEntity(user);
    return postRepository.save(post).toResponseDto();
  }

  public PostResponseDto getPost(Long id) {
    Post post = validationService.validatePost(id);
    return post.toResponseDto();
  }

  public List<PostResponseDto> getPosts() {
    List<Post> posts = postRepository.findAll();
    return posts.stream()
        .map(Post::toResponseDto)
        .toList();
  }

  public PostResponseDto updatePost(Long postId, PostRequestDto postRequestDto) {
    Post post = validationService.validatePost(postId);
    validationService.validatePostOwnership(post, postRequestDto.userId());

    post.setTitle(postRequestDto.title());
    post.setContent(postRequestDto.content());
    postRepository.save(post);
    return post.toResponseDto();
  }

  public void deletePost(Long postId, Long userId) {
    Post post = validationService.validatePost(postId);
    validationService.validatePostOwnership(post, userId);

    List<Comment> comments = commentRepository.findAllByPostId(postId);
    commentRepository.deleteAll(comments);

    postRepository.delete(post);
  }
}


