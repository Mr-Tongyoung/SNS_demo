package com.example.junho.sns_demo.domain.like.service;

import com.example.junho.sns_demo.domain.like.domain.Like;
import com.example.junho.sns_demo.domain.like.dto.LikeResponseDto;
import com.example.junho.sns_demo.domain.like.repository.LikeRepository;
import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.post.repository.PostRepository;
import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.domain.user.repository.UserRepository;
import com.example.junho.sns_demo.global.exception.CustomException;
import com.example.junho.sns_demo.global.exception.ErrorCode;
import com.example.junho.sns_demo.global.jwt.CustomUserDetails;
import com.example.junho.sns_demo.global.util.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final LikeRepository likeRepository;

  public LikeResponseDto likePost(Long postId, CustomUserDetails customUserDetails){
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    User user = userRepository.findById(customUserDetails.getId())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    Long userId = customUserDetails.getId();

    if(post.getLikedUsers().contains(userId)){
      throw new CustomException(ErrorCode.ALREADY_LIKED);
    }

    post.getLikedUsers().add(userId);
    post.upLike();
    postRepository.save(post);

    Like like = Like.builder()
        .post(post)
        .user(user)
        .build();
    likeRepository.save(like);

    return LikeResponseDto.builder()
        .postId(postId)
        .userId(userId)
        .likeCount(post.getLikes())
        .liked(true)
        .build();
  }

  public LikeResponseDto unlikePost(Long postId, CustomUserDetails customUserDetails) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

    Long userId = customUserDetails.getId();

    if (!post.getLikedUsers().contains(userId)) {
      throw new CustomException(ErrorCode.ALREADY_UNLIKED);
    }

    post.getLikedUsers().remove(userId);
    post.downLike();
    postRepository.save(post);

    Like like = likeRepository.findByPostIdAndUserId(postId, userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NO_SUCH_LIKE));
    likeRepository.delete(like);

    return LikeResponseDto.builder()
        .postId(postId)
        .userId(userId)
        .likeCount(post.getLikes())
        .liked(false)
        .build();
  }

}
