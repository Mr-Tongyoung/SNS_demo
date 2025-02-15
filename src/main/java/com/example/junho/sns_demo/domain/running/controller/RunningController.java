package com.example.junho.sns_demo.domain.running.controller;

import com.example.junho.sns_demo.domain.running.domain.RunningRecord;
import com.example.junho.sns_demo.domain.running.service.RunningService;
import com.example.junho.sns_demo.global.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/running")
@RequiredArgsConstructor
public class RunningController {

  private final RunningService runningService;

  @PostMapping("/start")
  public ResponseEntity<RunningRecord> startRunning(
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    Long userId = customUserDetails.getId(); // 로그인한 유저 ID 가져오기
    RunningRecord runningRecord = runningService.startRunning(userId);
    return ResponseEntity.ok(runningRecord);
  }

  @PostMapping("/end")
  public ResponseEntity<RunningRecord> endRunning(
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    Long userId = customUserDetails.getId(); // 로그인한 유저 ID 가져오기
    RunningRecord runningRecord = runningService.endRunning(userId);
    return ResponseEntity.ok(runningRecord);
  }
}
