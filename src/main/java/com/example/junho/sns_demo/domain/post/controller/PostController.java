package com.example.junho.sns_demo.domain.post.controller;

import com.example.junho.sns_demo.domain.post.dto.PostRequestDto;
import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import com.example.junho.sns_demo.domain.post.service.PostService;
import java.util.List;
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

  @GetMapping("/get/{postId}")
  public ResponseEntity<PostResponseDto> getPost(@PathVariable Long postId){
    PostResponseDto postResponseDto = postService.getPost(postId);
    return ResponseEntity.ok(postResponseDto);
  }

  @GetMapping("/getPosts")
  public ResponseEntity<List<PostResponseDto>> getPosts(){
    List<PostResponseDto> postResponseDtos = postService.getPosts();
    return ResponseEntity.ok(postResponseDtos);
  }

  @PutMapping("/update/{postId}")
  public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long postId, @RequestBody PostRequestDto postRequestDto){
    PostResponseDto postResponseDto = postService.updatePost(postId, postRequestDto);
    return ResponseEntity.ok(postResponseDto);
  }

  @DeleteMapping("/delete/{postId}")
  public ResponseEntity<Void> deletePost(@PathVariable Long postId, @RequestParam Long userId) {
    postService.deletePost(postId, userId);
    return ResponseEntity.noContent().build();
  }

}
