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

}
