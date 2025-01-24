package com.example.junho.sns_demo.domain.like.repository;

import com.example.junho.sns_demo.domain.like.domain.Like;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

  Optional<Like> findByPostIdAndUserId(Long postId, Long userId);
}
