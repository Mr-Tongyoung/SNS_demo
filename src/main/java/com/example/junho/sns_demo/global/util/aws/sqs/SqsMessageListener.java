//package com.example.junho.sns_demo.global.util.aws.sqs;
//
//import com.example.junho.sns_demo.domain.newsFeed.service.NewsfeedUpdateService;
//import com.example.junho.sns_demo.domain.post.domain.Post;
//import com.example.junho.sns_demo.domain.post.repository.PostRepository;
//import com.example.junho.sns_demo.global.exception.CustomException;
//import com.example.junho.sns_demo.global.exception.ErrorCode;
//import io.awspring.cloud.sqs.annotation.SqsListener;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class SqsMessageListener {
//
//  private final NewsfeedUpdateService newsfeedUpdateService;
//  private final PostRepository postRepository;
//  @SqsListener("NewsfeedCacheQueue")
//  public void processMessage(String message) {
//    System.out.println("Listener: " + message);
//    Post post = postRepository.findById(Long.parseLong(message))
//        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
//    // 팔로워 캐시 업데이트
//    System.out.println("Cache 업데이트");
//    newsfeedUpdateService.updateFollowerCaches(post.getUser().getId(), post.getId());
//  }
//}
//
