package com.example.junho.sns_demo.global.kafka;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "test-topic", groupId = "running-service-group")
public class KafkaConsumerService {
  @KafkaHandler
  public void listen(String message) {
    System.out.println("Kafka 메시지 수신됨: " + message);
  }
}

