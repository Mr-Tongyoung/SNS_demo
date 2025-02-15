package com.example.junho.sns_demo.domain.running.service;

import com.example.junho.sns_demo.domain.running.domain.RunningRecord;
import com.example.junho.sns_demo.domain.running.repository.RunningRecordRepository;
import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.domain.user.repository.UserRepository;
import com.example.junho.sns_demo.global.exception.CustomException;
import com.example.junho.sns_demo.global.exception.ErrorCode;
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
  public RunningRecord endRunning(Long authUserId) {
    // 현재 유저의 진행 중인 러닝 기록 찾기
    RunningRecord runningRecord = runningRecordRepository
        .findFirstByUserIdAndEndTimeIsNullOrderByStartTimeDesc(authUserId)
        .orElseThrow(() -> new IllegalStateException("진행 중인 러닝 기록이 없습니다."));

    // 러닝 기록이 본인의 것인지 확인 (이미 userId로 조회했기 때문에 추가 체크는 불필요)
    if (!runningRecord.getUser().getId().equals(authUserId)) {
      throw new IllegalArgumentException("자신의 러닝 기록만 종료할 수 있습니다.");
    }

    runningRecord.endRunning();
    runningRecordRepository.save(runningRecord);

    // 리더보드 업데이트 (초 단위 포인트 반영)
    rankingService.updateRanking(authUserId, runningRecord.getPoint());

    return runningRecord;
  }
}

