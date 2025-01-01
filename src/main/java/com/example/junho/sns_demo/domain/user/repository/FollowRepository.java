package com.example.junho.sns_demo.domain.user.repository;

import com.example.junho.sns_demo.domain.user.domain.Follow;
import com.example.junho.sns_demo.domain.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
  boolean existsByFollowerAndFollowing(User follower, User following);

  Optional<Follow> findByFollowerAndFollowing(User follower, User following);
}

