package com.example.junho.sns_demo.global.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageSender {
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private static final String TOPIC = "post-events";

  public void sendPostEvent(Long userId, Long postId) {
    Map<String, Object> message = new HashMap<>();
    message.put("userId", userId);
    message.put("postId", postId);

    try {
      String jsonMessage = objectMapper.writeValueAsString(message);
      kafkaTemplate.send(TOPIC, jsonMessage)
          .whenComplete((result, ex) -> {
            if (ex == null) {
              log.info("Kafka 메시지 전송 성공: {}", jsonMessage);
            } else {
              log.error("Kafka 메시지 전송 실패: {}", ex.getMessage(), ex);
            }
          });
      kafkaTemplate.flush();
    } catch (JsonProcessingException e) {
      log.error("Kafka 메시지 직렬화 실패", e);
    }
  }
}
