package com.example.junho.sns_demo.domain.post.controller;

import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import com.example.junho.sns_demo.domain.post.service.NewsfeedService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<List<PostResponseDto>> getNewsfeed(@RequestParam Long userId) {
    List<PostResponseDto> postResponseDtos = newsfeedService.getNewsfeed(userId);
    return ResponseEntity.ok(postResponseDtos);
  }

}
