package com.example.junho.sns_demo.global.util.aws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

  private final AmazonS3 amazonS3;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  public String uploadFile(MultipartFile file)
      throws IOException {
    // 파일 이름 생성
    String fileName = generateFileName(file);

    String contentType = determineContentType(file);

    // Content-Type 설정을 위한 ObjectMetadata 생성
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    metadata.setContentType(contentType);  // MultipartFile에서 Content-Type을 가져옴

    amazonS3.putObject(
        new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata));

    return amazonS3.getUrl(bucket, fileName).toString();
  }

  // delete
  public void deleteFile(String fileUrl) {
    try {
      // URL 디코딩
      String decodedUrl = URLDecoder.decode(fileUrl,
          StandardCharsets.UTF_8.name());
      String fileName = extractFileNameFromUrl(decodedUrl);
      amazonS3.deleteObject(bucket, fileName);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      // 예외 처리 로직 추가
    }
  }

  public void deleteFiles(List<String> fileUrls) {
    for (String fileUrl : fileUrls) {
      deleteFile(fileUrl);
    }
  }

  // 파일 이름 생성 메소드
  private String generateFileName(MultipartFile file) {
    return UUID.randomUUID() + "-" + file.getOriginalFilename();
  }

  // URL에서 파일 이름 추출 메소드
  private String extractFileNameFromUrl(String fileUrl) {
    int lastSlashIndex = fileUrl.lastIndexOf('/');
    if (lastSlashIndex == -1) {
      throw new IllegalArgumentException("Invalid file URL");
    }
    return fileUrl.substring(lastSlashIndex + 1);
  }

  // 파일의 확장자를 기반으로 Content-Type 결정
  private String determineContentType(MultipartFile file) {
    String contentType = URLConnection.guessContentTypeFromName(file.getOriginalFilename());
    if (contentType == null) {
      // 기본 Content-Type 설정 (파일 형식이 감지되지 않을 경우)
      contentType = "application/octet-stream";
    }
    return contentType;
  }

  public String generatePresignedUrl(String fileName, String contentType) {
    if (contentType == null || contentType.isEmpty()) {
      contentType = "application/octet-stream"; // 기본 Content-Type 설정
    }

    Date expiration = new Date();
    expiration.setTime(expiration.getTime() + 1000 * 60 * 10); // 10분 유효

    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(bucket, fileName)
            .withMethod(HttpMethod.PUT)
            .withExpiration(expiration);

    // Content-Type을 요청에 추가
    generatePresignedUrlRequest.addRequestParameter("Content-Type", contentType);

    return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
  }


}
