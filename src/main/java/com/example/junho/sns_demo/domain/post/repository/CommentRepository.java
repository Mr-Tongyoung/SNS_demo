package com.example.junho.sns_demo.domain.post.repository;

import com.example.junho.sns_demo.domain.post.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

}
