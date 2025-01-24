package com.example.junho.sns_demo.domain.comment.repository;

import com.example.junho.sns_demo.domain.comment.domain.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findAllByPostId(Long postId);
}
