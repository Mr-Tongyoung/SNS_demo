package com.example.junho.sns_demo.domain.user.service;

import com.example.junho.sns_demo.domain.user.domain.Follow;
import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.domain.user.repository.FollowRepository;
import com.example.junho.sns_demo.domain.user.repository.UserRepository;
import com.example.junho.sns_demo.global.exception.CustomException;
import com.example.junho.sns_demo.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    try {
      Follow follow = Follow.of(follower, following);
      follower.increaseFollowingCount();
      following.increaseFollowerCount();
      userRepository.save(follower);
      userRepository.save(following);
      followRepository.save(follow);
    } catch (DataIntegrityViolationException e) {
      throw new CustomException(ErrorCode.ALREADY_FOLLOWING);
    }
  }

  @Transactional
  public void followByIdForUsersInRange(Long followingId) {
    User following = userRepository.findById(followingId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    List<User> followers = userRepository.findAllByIdBetween(30000L, 80000L);
    List<Follow> followList = new ArrayList<>();

    for (User follower : followers) {
      try {
        Follow follow = Follow.of(follower, following);
        follower.increaseFollowingCount();
        followList.add(follow);
      } catch (DataIntegrityViolationException e) {
        // 중복 예외 무시
      }
    }

    // ✅ 배치 insert (대량 저장)
    followRepository.saveAll(followList);

    // ✅ following의 팔로워 수 업데이트
    following.setFollowerCount(following.getFollowerCount() + followList.size());
    userRepository.save(following);
  }



  public void unfollow(Long followerId, Long followingId) {
    User follower = userRepository.findById(followerId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    User following = userRepository.findById(followingId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
        .orElseThrow(() -> new CustomException(ErrorCode.NO_SUCH_FOLLOWERSHIP));

    follower.decreaseFollowingCount();
    following.decreaseFollowerCount();
    userRepository.save(follower);
    userRepository.save(following);
    followRepository.delete(follow);
  }
}

