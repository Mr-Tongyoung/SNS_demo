package com.example.junho.sns_demo.domain.running.service;

import com.example.junho.sns_demo.domain.running.dto.RankingEntry;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingService {

  private final RedisTemplate<String, String> redisTemplate;
  private static final String LEADERBOARD_KEY_PREFIX = "leaderboard:daily:";

  /**
   * 랭킹 업데이트 (사용자가 러닝을 종료하면 점수를 추가)
   */
  public void updateRanking(Long userId, int point) {
    String leaderboardKey = getDailyLeaderboardKey();
    redisTemplate.opsForZSet().incrementScore(leaderboardKey, userId.toString(), point);
    redisTemplate.expire(leaderboardKey, Duration.ofDays(1).plusHours(6)); // TTL 설정 (하루 + 6시간)
  }

  /**
   * 상위 n명 랭킹 조회
   */
  public List<RankingEntry> getTopRankings(int limit) {
    String leaderboardKey = getDailyLeaderboardKey();
    Set<TypedTuple<String>> rankings =
        redisTemplate.opsForZSet().reverseRangeWithScores(leaderboardKey, 0, limit - 1);

    if (rankings == null) return Collections.emptyList();

    return rankings.stream()
        .map(entry -> new RankingEntry(Long.parseLong(entry.getValue()), entry.getScore().intValue()))
        .collect(Collectors.toList());
  }

  /**
   * 특정 유저의 랭킹 조회
   */
  public RankingEntry getUserRanking(Long userId) {
    String leaderboardKey = getDailyLeaderboardKey();
    Double score = redisTemplate.opsForZSet().score(leaderboardKey, userId.toString());
    Long rank = redisTemplate.opsForZSet().reverseRank(leaderboardKey, userId.toString());

    if (score == null || rank == null) {
      return new RankingEntry(userId, 0, -1);
    }
    return new RankingEntry(userId, score.intValue(), (int) (rank + 1));
  }

  /**
   * 현재 날짜에 맞는 리더보드 키 생성
   */
  private String getDailyLeaderboardKey() {
    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    return LEADERBOARD_KEY_PREFIX + today;
  }
}


