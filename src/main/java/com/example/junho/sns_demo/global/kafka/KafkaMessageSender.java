package com.example.junho.sns_demo.global.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaMessageSender {
  private final KafkaTemplate<String, String> kafkaTemplate;
  private static final String TOPIC = "post-events";

  public void sendPostEvent(String postId) {
    kafkaTemplate.send(TOPIC, postId)
        .whenComplete((result, ex) -> {
          if (ex == null) {
            System.out.println("Kafka 메시지 전송 성공 (Post ID): " + postId);
          } else {
            System.err.println("Kafka 메시지 전송 실패: " + ex.getMessage());
          }
        });
    kafkaTemplate.flush(); // ✅ 즉시 메시지 전송
  }
}
