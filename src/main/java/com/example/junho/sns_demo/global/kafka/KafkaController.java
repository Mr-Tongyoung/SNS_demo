package com.example.junho.sns_demo.global.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class KafkaController {
  private final KafkaProducerService kafkaProducerService;

  @GetMapping("/send")
  public ResponseEntity<String> sendMessage(@RequestParam String message) {
    kafkaProducerService.sendMessage("test-topic", message);
    return ResponseEntity.ok("메시지 전송 완료!");
  }
}

