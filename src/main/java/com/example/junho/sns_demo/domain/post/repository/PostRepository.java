package com.example.junho.sns_demo.domain.post.repository;

import com.example.junho.sns_demo.domain.post.domain.MediaFile;
import com.example.junho.sns_demo.domain.post.domain.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  @Query("SELECT p FROM Post p LEFT JOIN FETCH p.mediaFiles WHERE p.id = :postId")
  Optional<Post> findWithMediaFilesById(@Param("postId") Long postId);

  @Query("SELECT m FROM MediaFile m WHERE m.post.id = :postId")
  List<MediaFile> findMediaFilesByPostId(@Param("postId") Long postId);

//  @Query(
//      "SELECT DISTINCT p FROM Post p " +
//          "JOIN FETCH p.user u " +
//          "LEFT JOIN FETCH p.mediaFiles fm " +
//          "WHERE u.id IN (" +
//          "  SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId" +
//          ") " +
//          "AND u.isCeleb = false " +  // 인플루언서가 아닌 유저만 필터링
//          "ORDER BY p.createdAt DESC"
//  )
//  List<Post> findFeedPostsWithFileMediaByUserId(@Param("userId") Long userId);

  @Query(
      "SELECT DISTINCT p FROM Post p " +
          "JOIN FETCH p.user u " +
          "LEFT JOIN FETCH p.mediaFiles fm " +
          "WHERE u.id IN (" +
          "  SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId" +
          ") " +
          "AND u.isCeleb = false " +  // 인플루언서가 아닌 유저만 필터링
          "ORDER BY p.createdAt DESC"
  )
  List<Post> findFeedPostsWithFileMediaByUserId(@Param("userId") Long userId, Pageable pageable);


//  @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.mediaFiles WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
//  List<Post> findFeedPostsWithFileMediaByUserId(@Param("userId") Long userId);



  @Query(
      "SELECT DISTINCT p FROM Post p " +
          "JOIN FETCH p.user u " +
          "LEFT JOIN FETCH p.mediaFiles fm " +
          "WHERE u.id IN (" +
          "  SELECT f.following.id FROM Follow f " +
          "  WHERE f.follower.id = :userId" +
          ") " +
          "AND u.isCeleb = true " +
          "ORDER BY p.createdAt DESC"
  )
  List<Post> findInfluencerPosts(@Param("userId") Long userId);



  @Query(
      "SELECT p FROM Post p " +
          "WHERE p.user.id IN (" +
          "  SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId" +
          ") ORDER BY p.createdAt DESC"
  )
  List<Post> findFeedPostsByUserIdWithoutFetchJoin(@Param("userId") Long userId, Pageable pageable);


  // 1. User + Post Fetch Join (User 기준으로 가져오기 때문에 중복 가능 → DISTINCT 필수)
  @Query("""
    SELECT DISTINCT p FROM Post p
    JOIN FETCH p.user u
    WHERE u.id IN (
        SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId
    )
    AND u.isCeleb = false
    ORDER BY p.createdAt DESC
""")
  List<Post> findPostsWithUserAndFilterByFollow(@Param("userId") Long userId, Pageable pageable);

  // 2. MediaFile 일괄 조회 (IN 쿼리로 N+1 방지)
  @Query("SELECT m FROM MediaFile m WHERE m.post.id IN :postIds")
  List<MediaFile> findMediaFilesByPostIds(@Param("postIds") List<Long> postIds);

  // 2. MediaFile 일괄 조회 (페치조인으로 N+1 방지)
  @Query("""
    SELECT DISTINCT p FROM Post p
    LEFT JOIN FETCH p.mediaFiles
    WHERE p.id IN :postIds
""")
  List<Post> findPostsWithMediaByPostIds(@Param("postIds") List<Long> postIds);


  @Query("SELECT p FROM Post p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
  List<Post> findPostsByUserId(@Param("userId") Long userId, Pageable pageable);




  @Query("SELECT p FROM Post p WHERE p.user.id IN :userIds ORDER BY p.createdAt DESC")
  List<Post> findPostsByUserIds(@Param("userIds") List<Long> userIds, Pageable pageable);


}
