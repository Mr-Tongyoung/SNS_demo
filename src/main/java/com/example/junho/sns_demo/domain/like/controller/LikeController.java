package com.example.junho.sns_demo.domain.like.controller;

import com.example.junho.sns_demo.domain.like.dto.LikeRequestDto;
import com.example.junho.sns_demo.domain.like.dto.LikeResponseDto;
import com.example.junho.sns_demo.domain.like.service.LikeService;
import com.example.junho.sns_demo.global.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {

  private final LikeService likeService;

  @PostMapping("/add/{postId}")
  public ResponseEntity<LikeResponseDto> addLike(@PathVariable Long postId,
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    LikeResponseDto likeResponseDto = likeService.likePost(postId, customUserDetails);
    return ResponseEntity.ok(likeResponseDto);
  }

  @DeleteMapping("/delete/{postId}")
  public ResponseEntity<LikeResponseDto> deleteLike(@PathVariable Long postId,
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    LikeResponseDto likeResponseDto = likeService.unlikePost(postId, customUserDetails);
    return ResponseEntity.ok(likeResponseDto);
  }


}
