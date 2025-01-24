package com.example.junho.sns_demo.domain.comment.controller;

import com.example.junho.sns_demo.domain.comment.dto.CommentRequestDto;
import com.example.junho.sns_demo.domain.comment.dto.CommentResponseDto;
import com.example.junho.sns_demo.domain.comment.service.CommentService;
import com.example.junho.sns_demo.global.jwt.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

  public final CommentService commentService;

  @PostMapping("/create")
  public ResponseEntity<CommentResponseDto> createComment(
      CommentRequestDto commentRequestDto,
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    CommentResponseDto commentResponseDto = commentService.createComment(
        commentRequestDto, customUserDetails);
    return ResponseEntity.ok(commentResponseDto);
  }

  @GetMapping("/getAll")
  public ResponseEntity<List<CommentResponseDto>> getAllComment(
      @RequestParam Long postId) {
    List<CommentResponseDto> commentResponseDtoList = commentService.getComments(
        postId);
    return ResponseEntity.ok(commentResponseDtoList);
  }

  @PutMapping("/update")
  public ResponseEntity<CommentResponseDto> updateComment(
      @RequestParam Long commentId,
      @RequestBody CommentRequestDto commentRequestDto,
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    CommentResponseDto commentResponseDto = commentService.updateComment(
        commentId, commentRequestDto, customUserDetails);
    return ResponseEntity.ok(commentResponseDto);
  }

  @DeleteMapping("/delete")
  public ResponseEntity<Void> deleteComment(
      @RequestParam Long commentId,
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    commentService.deleteComment(commentId, customUserDetails.getId());
    return ResponseEntity.noContent().build();
  }
}
