package com.example.junho.sns_demo.domain.post.service;

import com.example.junho.sns_demo.domain.newsFeed.service.NewsfeedUpdateService;
import com.example.junho.sns_demo.global.elasticSearch.ElasticRepository;
import com.example.junho.sns_demo.global.elasticSearch.PostDocument;
import com.example.junho.sns_demo.domain.comment.domain.Comment;
import com.example.junho.sns_demo.domain.post.domain.MediaFile;
import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.post.dto.PostRequestDto;
import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import com.example.junho.sns_demo.domain.comment.repository.CommentRepository;
import com.example.junho.sns_demo.domain.post.repository.MediaFileRepository;
import com.example.junho.sns_demo.domain.post.repository.PostRepository;
import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.domain.user.repository.UserRepository;
import com.example.junho.sns_demo.global.exception.CustomException;
import com.example.junho.sns_demo.global.exception.ErrorCode;
import com.example.junho.sns_demo.global.jwt.CustomUserDetails;
import com.example.junho.sns_demo.global.kafka.KafkaMessageSender;
import com.example.junho.sns_demo.global.util.aws.s3.S3Service;
import com.example.junho.sns_demo.global.util.ValidationService;
//import com.example.junho.sns_demo.global.util.aws.sqs.SqsMessageSender;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

  private final ValidationService validationService;
  private final PostRepository postRepository;
  private final MediaFileRepository mediaFileRepository;
  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final ElasticRepository elasticRepository;
  private final S3Service s3Service;
  private final KafkaMessageSender kafkaMessageSender;

  @Transactional
  public PostResponseDto createPost(PostRequestDto postRequestDto,
      List<MultipartFile> mediaFiles, CustomUserDetails customUserDetails)
      throws IOException {
    User user = userRepository.findById(customUserDetails.getId())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    if(user.isCeleb())
      return createCelebrityUserPost(postRequestDto, mediaFiles, customUserDetails);
    else
      return createNormalUserPost(postRequestDto, mediaFiles, customUserDetails);
  }

  public PostResponseDto createNormalUserPost(PostRequestDto postRequestDto,
      List<MultipartFile> mediaFiles, CustomUserDetails customUserDetails)
      throws IOException {
    User user = userRepository.findById(customUserDetails.getId())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
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

    // Elasticsearch에 저장
    PostDocument postDocument = PostDocument.builder()
        .id(post.getId())
        .userId(user.getId())
        .content(post.getContent())
        .likeCount(post.getLikes())
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .build();

    elasticRepository.save(postDocument);

    savedPost = postRepository.save(savedPost);

    // ✅ Kafka 전송
    kafkaMessageSender.sendPostEvent(savedPost.getUser().getId(), savedPost.getId());
    return savedPost.toResponseDto();
  }

  public PostResponseDto createCelebrityUserPost(PostRequestDto postRequestDto,
      List<MultipartFile> mediaFiles, CustomUserDetails customUserDetails)
      throws IOException {
    User user = userRepository.findById(customUserDetails.getId())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
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

    // Elasticsearch에 저장
    PostDocument postDocument = PostDocument.builder()
        .id(post.getId())
        .userId(user.getId())
        .content(post.getContent())
        .likeCount(post.getLikes())
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .build();

    elasticRepository.save(postDocument);

    savedPost = postRepository.save(savedPost);

    return savedPost.toResponseDto();
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

  public List<PostDocument> searchPostsByKeyword(String keyword) {
    return elasticRepository.findByContentContaining(keyword);
  }
}
