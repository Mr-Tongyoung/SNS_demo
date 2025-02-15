package com.example.junho.sns_demo.domain.running.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RankingEntry {
  private Long userId;
  private int point;
  private int rank; // 기본 rank 조회 시 사용 (없으면 -1)

  public RankingEntry(Long userId, int point) {
    this.userId = userId;
    this.point = point;
    this.rank = -1; // 기본값 설정
  }
}

