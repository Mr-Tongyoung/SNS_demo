package com.example.junho.sns_demo.domain.post.service;

import com.example.junho.sns_demo.domain.post.domain.Comment;
import com.example.junho.sns_demo.domain.post.domain.MediaFile;
import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.post.dto.PostRequestDto;
import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import com.example.junho.sns_demo.domain.post.repository.CommentRepository;
import com.example.junho.sns_demo.domain.post.repository.MediaFileRepository;
import com.example.junho.sns_demo.domain.post.repository.PostRepository;
import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.domain.user.repository.UserRepository;
import com.example.junho.sns_demo.global.jwt.CustomUserDetails;
import com.example.junho.sns_demo.domain.newsFeed.service.NewsfeedUpdateService;
import com.example.junho.sns_demo.global.util.aws.S3Service;
import com.example.junho.sns_demo.global.util.ValidationService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostService {

  private final ValidationService validationService;
  private final PostRepository postRepository;
  private final MediaFileRepository mediaFileRepository;
  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final S3Service s3Service;
  private final NewsfeedUpdateService newsfeedUpdateService;

  @Transactional
  public PostResponseDto createPost(PostRequestDto postRequestDto,
      List<MultipartFile> mediaFiles, CustomUserDetails customUserDetails)
      throws IOException {
    User user = userRepository.findByUsername(customUserDetails.getUsername());
    customUserDetails.getUser().setId(user.getId());
    // 유효한 사용자 확인
    validationService.validateUser(customUserDetails.getId());

    // Post 객체 생성 및 저장
    Post post = postRequestDto.toEntity(user);
    post.setMediaFiles(new ArrayList<>()); // mediaFiles 초기화
    Post savedPost = postRepository.save(post); // Post 먼저 저장

    // MediaFile 추가
    if (mediaFiles != null && !mediaFiles.isEmpty()) {
      for (MultipartFile file : mediaFiles) {
        if (!file.isEmpty()) {
          String mediaUrl = s3Service.uploadFile(file);
          MediaFile mediaFile = MediaFile.builder()
              .post(savedPost) // 저장된 Post와 연결
              .url(mediaUrl)
              .build();

          savedPost.addMediaFile(mediaFile); // Post에 MediaFile 추가
          mediaFileRepository.save(mediaFile); // MediaFile 명시적으로 저장
        }
      }
    }

    savedPost = postRepository.save(savedPost);

    // 팔로워 캐시 업데이트
    newsfeedUpdateService.updateFollowerCaches(user.getId(), savedPost.getId());

    return savedPost.toResponseDto();
  }

  @Transactional
  public void createPosts(PostRequestDto postRequestDto,
      List<MultipartFile> mediaFiles, CustomUserDetails customUserDetails)
      throws IOException {
    for (int i = 0; i < 100; i++) {
      createPost(postRequestDto,mediaFiles,customUserDetails);
    }
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

  public List<String> getMediaFileUrls(Long postId) {
    Post post = validationService.validatePost(postId);
    List<MediaFile> mediaFiles = post.getMediaFiles();
    List<String> urls = new ArrayList<>();
    for (MediaFile mediaFile : mediaFiles) {
      urls.add(mediaFile.getUrl());
    }
    return urls;
  }

  public PostResponseDto updatePost(Long postId, PostRequestDto postRequestDto,
      CustomUserDetails customUserDetails) {
    Post post = validationService.validatePost(postId);
    validationService.validatePostOwnership(post, customUserDetails.getUser()
        .getId());

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

  public void addLikeToPost(Long postId, CustomUserDetails customUserDetails) {
    User user = userRepository.findByUsername(customUserDetails.getUsername());
    customUserDetails.getUser().setId(user.getId());
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    if (post.getLikedUsers().contains(user)) {
      throw new IllegalStateException("User already liked this post.");
    }
    post.like(user);
    postRepository.save(post);
  }

  @Transactional
  public Post addMediaToPost(Long postId, List<MultipartFile> mediaFiles)
      throws IOException {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("Post not found"));

    for (MultipartFile file : mediaFiles) {
      String mediaUrl = s3Service.uploadFile(file);
      MediaFile mediaFile = MediaFile.builder()
          .url(mediaUrl)
          .post(post)
          .build();
      post.addMediaFile(mediaFile);
    }

    return postRepository.save(post);
  }
}
