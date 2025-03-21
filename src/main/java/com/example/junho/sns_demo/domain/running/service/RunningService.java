package com.example.junho.sns_demo.domain.running.service;

import com.example.junho.sns_demo.domain.running.domain.RunningRecord;
import com.example.junho.sns_demo.domain.running.repository.RunningRecordRepository;
import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.domain.user.repository.UserRepository;
import com.example.junho.sns_demo.global.exception.CustomException;
import com.example.junho.sns_demo.global.exception.ErrorCode;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RunningService {

  private final RunningRecordRepository runningRecordRepository;
  private final UserRepository userRepository;
  private final RankingService rankingService;

  @Transactional
  public RunningRecord startRunning(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    RunningRecord runningRecord = new RunningRecord();
    runningRecord.setUser(user);
    runningRecord.startRunning();
    return runningRecordRepository.save(runningRecord);
  }

  @Transactional
  public RunningRecord endRunning(Long userId) {
    // 현재 유저의 진행 중인 러닝 기록 찾기
    RunningRecord runningRecord = runningRecordRepository
        .findFirstByUserIdAndEndTimeIsNullOrderByStartTimeDesc(userId)
        .orElseThrow(() -> new IllegalStateException("진행 중인 러닝 기록이 없습니다."));

    // 러닝 기록이 본인의 것인지 확인 (이미 userId로 조회했기 때문에 추가 체크는 불필요)
    if (!runningRecord.getUser().getId().equals(userId)) {
      throw new IllegalArgumentException("자신의 러닝 기록만 종료할 수 있습니다.");
    }

    runningRecord.endRunning();
    runningRecordRepository.save(runningRecord);

    // 리더보드 업데이트 (초 단위 포인트 반영)
    rankingService.updateRanking(userId, runningRecord.getPoint());

    return runningRecord;
  }

  /**
   * ✅ 여러 유저 중에서 랜덤한 유저를 선택하여 러닝 시작 -> 1~60초 대기 -> 종료
   */
  @Transactional
  public RunningRecord startAndEndRunningWithDelayForRandomUser() {
    // ✅ 1. DB에서 랜덤한 유저 ID 가져오기
    List<Long> userIds = userRepository.findAllUserIds();  // userId 리스트 가져오기
    if (userIds.isEmpty()) {
      throw new IllegalStateException("등록된 유저가 없습니다.");
    }

    Long randomUserId = userIds.get(ThreadLocalRandom.current().nextInt(userIds.size()));
    System.out.println("🎯 Selected Random User ID: " + randomUserId);

    // ✅ 2. 러닝 시작
    RunningRecord startRecord = startRunning(randomUserId);

    // ✅ 3. 1~60초 사이의 랜덤 시간 대기
    int sleepTime = ThreadLocalRandom.current().nextInt(1, 61) * 1000;
    try {
      Thread.sleep(sleepTime);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread sleep interrupted", e);
    }

    // ✅ 4. 러닝 종료
    RunningRecord endRecord = endRunning(randomUserId);
    System.out.println("✅ Running ended for user " + randomUserId);

    return endRecord;
  }
}

