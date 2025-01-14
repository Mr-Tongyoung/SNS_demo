package com.example.junho.sns_demo.domain.post.repository;

import com.example.junho.sns_demo.domain.post.domain.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.user.id IN :userIds ORDER BY p.createdAt DESC")
  List<Post> findPostsWithUsersByUserIdsOrderByCreatedAtDesc(@Param("userIds") List<Long> userIds);

  @Query(
      "SELECT p FROM Post p " +
          "JOIN FETCH p.user u " +
          "WHERE u.id IN (" +
          "  SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId" +
          ") ORDER BY p.createdAt DESC"
  )
  List<Post> findFeedPostsByUserId(@Param("userId") Long userId);

  @Query(
      "SELECT DISTINCT p FROM Post p " +
          "JOIN FETCH p.user u " +
          "LEFT JOIN FETCH p.mediaFiles fm " + // LEFT JOIN FETCH를 사용
          "WHERE u.id IN (" +
          "  SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId" +
          ") ORDER BY p.createdAt DESC"
  )
  List<Post> findFeedPostsWithFileMediaByUserId(@Param("userId") Long userId);
}
