package com.example.junho.sns_demo.domain.user.repository;

import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.user.domain.Follow;
import com.example.junho.sns_demo.domain.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {

  boolean existsByFollowerAndFollowing(User follower, User following);

  Optional<Follow> findByFollowerAndFollowing(User follower, User following);

  /**
   * 특정 사용자를 팔로우하는 모든 팔로워의 ID를 가져옵니다.
   *
   * @param userId 팔로잉 대상 사용자 ID
   * @return 팔로워들의 사용자 ID 리스트
   */
  @Query("SELECT f.follower.id FROM Follow f WHERE f.following.id = :userId")
  List<Long> findFollowers(@Param("userId") Long userId);

  /**
   * 특정 사용자가 팔로우하는 모든 사용자 ID를 가져옵니다.
   *
   * @param userId 팔로워 사용자 ID
   * @return 팔로우 대상 사용자 ID 리스트
   */
  @Query("SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId")
  List<Long> findFollowings(@Param("userId") Long userId);


  @Query("SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId")
  List<Long> findFollowingIdsByUserId(@Param("userId") Long userId);

}


