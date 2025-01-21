package com.example.junho.sns_demo.domain.post.controller;

import com.example.junho.sns_demo.domain.elasticSearch.PostDocument;
import com.example.junho.sns_demo.domain.post.dto.PostRequestDto;
import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import com.example.junho.sns_demo.domain.post.service.PostService;
import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.global.jwt.CustomUserDetails;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

  private final PostService postService;

  @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<PostResponseDto> createPost(@RequestPart("postRequestDto") PostRequestDto postRequestDto, // JSON 데이터
      @RequestPart(value = "mediaFile", required = false) List<MultipartFile> mediaFiles,
      @AuthenticationPrincipal CustomUserDetails customUserDetails)
      throws IOException {
    PostResponseDto postResponseDto = postService.createPost(postRequestDto, mediaFiles, customUserDetails);
    return ResponseEntity.ok(postResponseDto);
  }

  @PostMapping(value = "/createPosts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> createPosts(@RequestPart("postRequestDto") PostRequestDto postRequestDto, // JSON 데이터
      @RequestPart(value = "mediaFile", required = false) List<MultipartFile> mediaFiles,
      @AuthenticationPrincipal CustomUserDetails customUserDetails)
      throws IOException {
    postService.createPosts(postRequestDto, mediaFiles, customUserDetails);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/get/{postId}")
  public ResponseEntity<PostResponseDto> getPost(@PathVariable Long postId){
    PostResponseDto postResponseDto = postService.getPost(postId);
    return ResponseEntity.ok(postResponseDto);
  }

  @GetMapping("/getMediaUrls")
  public List<String> getMediaUrls(@RequestParam("postId") Long postId){
    return postService.getMediaFileUrls(postId);
  }

  @GetMapping("/getPosts")
  public ResponseEntity<List<PostResponseDto>> getPosts(){
    List<PostResponseDto> postResponseDtos = postService.getPosts();
    return ResponseEntity.ok(postResponseDtos);
  }

  @PutMapping("/update/{postId}")
  public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long postId, @RequestBody PostRequestDto postRequestDto,
      @AuthenticationPrincipal CustomUserDetails customUserDetails){
    PostResponseDto postResponseDto = postService.updatePost(postId, postRequestDto, customUserDetails);
    return ResponseEntity.ok(postResponseDto);
  }

  @DeleteMapping("/delete/{postId}")
  public ResponseEntity<Void> deletePost(@PathVariable Long postId, @RequestParam Long userId) {
    postService.deletePost(postId, userId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/like")
  public ResponseEntity<PostResponseDto> likePost(@RequestParam Long postId,
      @AuthenticationPrincipal CustomUserDetails customUserDetails){
    postService.addLikeToPost(postId, customUserDetails);
    return ResponseEntity.ok(postService.getPost(postId));
  }

  @GetMapping("/search")
  public List<PostDocument> searchPosts(@RequestParam String keyword) {
    return postService.searchPostsByKeyword(keyword);
  }
}
