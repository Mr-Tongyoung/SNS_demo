package com.example.junho.sns_demo.domain.user.controller;

import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.domain.user.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

  private final FollowService followService;

  @PostMapping
  public ResponseEntity<String> follow(@RequestParam Long followerId,
      @RequestParam Long followingId) {
    followService.follow(followerId, followingId);
    return ResponseEntity.ok("Followed successfully");
  }

  @DeleteMapping("/unfollow")
  public ResponseEntity<String> unfollow(@RequestParam Long followerId,
      @RequestParam Long followingId) {
    followService.unfollow(followerId, followingId);
    return ResponseEntity.ok("Unfollowed successfully");
  }
}
