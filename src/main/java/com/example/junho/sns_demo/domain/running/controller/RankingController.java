package com.example.junho.sns_demo.domain.running.controller;

import com.example.junho.sns_demo.domain.running.dto.RankingEntry;
import com.example.junho.sns_demo.domain.running.service.RankingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor
public class RankingController {

  private final RankingService rankingService;

  @GetMapping("/top/{limit}")
  public ResponseEntity<List<RankingEntry>> getTopRankings(@PathVariable int limit) {
    return ResponseEntity.ok(rankingService.getTopRankings(limit));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<RankingEntry> getUserRanking(@PathVariable Long userId) {
    return ResponseEntity.ok(rankingService.getUserRanking(userId));
  }
}

