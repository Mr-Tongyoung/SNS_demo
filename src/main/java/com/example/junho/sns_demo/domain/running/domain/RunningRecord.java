package com.example.junho.sns_demo.domain.running.domain;

import com.example.junho.sns_demo.domain.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RunningRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private int point; // 러닝 종료 시점에 저장될 포인트 (분 단위)

  public void startRunning() {
    this.startTime = LocalDateTime.now();
    this.point = 0; // 시작 시 초기화
  }

  public void endRunning() {
    this.endTime = LocalDateTime.now();
    this.point = (int) Duration.between(this.startTime, this.endTime).getSeconds();
  }
}

