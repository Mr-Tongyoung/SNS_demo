package com.example.junho.sns_demo.global.util.aws.s3;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3Controller {

  private final S3Service s3Service;

  @CrossOrigin(origins = "http://localhost:5173") // 프론트엔드 도메인
  @PostMapping("/presigned-url")
  public ResponseEntity<Map<String, String>> generatePresignedUrl(@RequestBody PresignedUrlRequest request) {
    // 디버깅: 요청 데이터를 확인
    System.out.println("Request Body: fileName=" + request.fileName() + ", contentType=" + request.contentType());

    if (request.fileName() == null || request.contentType() == null) {
      throw new IllegalArgumentException("fileName and contentType are required!");
    }

    String presignedUrl = s3Service.generatePresignedUrl(request.fileName(), request.contentType());
    return ResponseEntity.ok(Map.of("url", presignedUrl));
  }
}
