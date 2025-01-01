package com.example.junho.sns_demo.domain.user.service;

import com.example.junho.sns_demo.domain.user.domain.Follow;
import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.domain.user.repository.FollowRepository;
import com.example.junho.sns_demo.domain.user.repository.UserRepository;
import com.example.junho.sns_demo.global.exception.CustomException;
import com.example.junho.sns_demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

  private final UserRepository userRepository;
  private final FollowRepository followRepository;

  public void follow(Long followerId, Long followingId) {
    User follower = userRepository.findById(followerId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    User following = userRepository.findById(followingId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    if (followRepository.existsByFollowerAndFollowing(follower, following)) {
      throw new CustomException(ErrorCode.ALREADY_FOLLOWING);
    }

    Follow follow = Follow.of(follower, following);
    followRepository.save(follow);
  }

  public void unfollow(Long followerId, Long followingId) {
    User follower = userRepository.findById(followerId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    User following = userRepository.findById(followingId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
        .orElseThrow(() -> new CustomException(ErrorCode.NO_SUCH_FOLLOWERSHIP));

    followRepository.delete(follow);
  }
}
