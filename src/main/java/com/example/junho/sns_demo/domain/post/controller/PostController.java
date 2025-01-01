package com.example.junho.sns_demo.domain.post.controller;

import com.example.junho.sns_demo.domain.post.dto.PostRequestDto;
import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import com.example.junho.sns_demo.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

  private final PostService postService;

  @PostMapping("/create")
  public ResponseEntity<PostResponseDto> createPost(@RequestBody PostRequestDto postRequestDto){
    PostResponseDto postResponseDto = postService.createPost(postRequestDto);
    return ResponseEntity.ok(postResponseDto);
  }

  @GetMapping("/get/{id}")
  public ResponseEntity<PostResponseDto> getPost(@PathVariable Long id){
    PostResponseDto postResponseDto = postService.getPost(id);
    return ResponseEntity.ok(postResponseDto);
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id, @RequestBody PostRequestDto postRequestDto){
    PostResponseDto postResponseDto = postService.updatePost(id, postRequestDto);
    return ResponseEntity.ok(postResponseDto);
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<PostResponseDto> deletePost(@PathVariable Long id, @RequestParam Long userId) {
    PostResponseDto postResponseDto = postService.deletePost(id, userId);
    return ResponseEntity.ok(postResponseDto);
  }

}
