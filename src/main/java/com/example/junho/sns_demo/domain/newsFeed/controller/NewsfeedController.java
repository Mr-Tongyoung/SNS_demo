package com.example.junho.sns_demo.domain.newsFeed.controller;

import com.example.junho.sns_demo.domain.newsFeed.service.NewsfeedService;
import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import com.example.junho.sns_demo.global.jwt.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/newsfeed")
public class NewsfeedController {

  private final NewsfeedService newsfeedService;

  @GetMapping
  public ResponseEntity<List<PostResponseDto>> getNewsfeed(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
    List<PostResponseDto> postResponseDtos = newsfeedService.getNewsfeed(customUserDetails);
    return ResponseEntity.ok(postResponseDtos);
  }

  @GetMapping("/withoutJWT")
  public ResponseEntity<List<PostResponseDto>> getNewsfeedWithoutJWT(@RequestParam Long userId) {
    List<PostResponseDto> postResponseDtos = newsfeedService.getNewsfeedWithoutJWT(userId);
    return ResponseEntity.ok(postResponseDtos);
  }

  @GetMapping("/withoutFJ")
  public ResponseEntity<List<PostResponseDto>> getNewsfeedWithoutFJ(@RequestParam Long userId) {
    List<PostResponseDto> postResponseDtos = newsfeedService.getNewsfeedWithoutFJ(userId);
    return ResponseEntity.ok(postResponseDtos);
  }

  @GetMapping("/withoutCache")
  public ResponseEntity<List<PostResponseDto>> getNewsfeedWithoutCache(@RequestParam Long userId) {
    List<PostResponseDto> postResponseDtos = newsfeedService.getNewsfeedWithoutCache(userId);
    return ResponseEntity.ok(postResponseDtos);
  }
}
