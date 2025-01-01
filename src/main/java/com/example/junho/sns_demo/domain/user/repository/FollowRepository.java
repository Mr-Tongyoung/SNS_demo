package com.example.junho.sns_demo.domain.user.repository;

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

  @Query("SELECT f.following.id FROM Follow f WHERE f.follower.id = :followerId")
  List<Long> findFollowingIdsByFollowerId(@Param("followerId") Long followerId);
}

