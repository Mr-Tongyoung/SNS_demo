package com.example.junho.sns_demo.global.kafka;

import com.example.junho.sns_demo.domain.newsFeed.service.NewsfeedUpdateService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KafkaMessageConsumer {

  private final NewsfeedUpdateService newsfeedUpdateService;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "post-events",
      groupId = "post-processing-group",
      concurrency = "3"
  )
  public void consumePostEvent(String message) {
    try {
      JsonNode jsonNode = objectMapper.readTree(message);
      Long userId = jsonNode.get("userId").asLong();
      Long postId = jsonNode.get("postId").asLong();

      log.info("Kafka 메시지 수신됨 - userId: {}, postId: {}", userId, postId);
      newsfeedUpdateService.updateFollowerCaches(userId, postId);
    } catch (Exception e) {
      log.error("Kafka 메시지 파싱 또는 처리 실패: {}", message, e);
    }
  }
}
