package com.example.junho.sns_demo.global.kafka;

import com.example.junho.sns_demo.domain.newsFeed.service.NewsfeedUpdateService;
import com.example.junho.sns_demo.domain.post.repository.PostRepository;
import com.example.junho.sns_demo.domain.newsFeed.service.NewsfeedCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageConsumer {

  private final PostRepository postRepository;
  private final NewsfeedUpdateService newsfeedUpdateService;

  @KafkaListener(
      topics = "post-events",
      groupId = "post-processing-group",
      concurrency = "3" // ✅ 병렬 Consumer 처리
  )
  public void consumePostEvent(String postId) {
    log.info("Kafka 메시지 수신됨 (Post ID): {}", postId);

    postRepository.findById(Long.parseLong(postId)).ifPresent(post -> {
      // ✅ 뉴스피드 캐시 업데이트
      newsfeedUpdateService.updateFollowerCaches(post.getUser().getId(), post.getId());
    });
  }
}
