package com.example.junho.sns_demo.domain.user.controller;

import com.example.junho.sns_demo.domain.user.dto.UserRequestDto;
import com.example.junho.sns_demo.domain.user.dto.UserResponseDto;
import com.example.junho.sns_demo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRequestDto requestDto) {
    UserResponseDto responseDto = userService.registerUser(requestDto);
    return ResponseEntity.ok(responseDto);
  }
}