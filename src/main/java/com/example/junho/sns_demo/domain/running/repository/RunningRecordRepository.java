package com.example.junho.sns_demo.domain.running.repository;

import com.example.junho.sns_demo.domain.running.domain.RunningRecord;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunningRecordRepository extends JpaRepository<RunningRecord, Long> {

  Optional<RunningRecord> findFirstByUserIdAndEndTimeIsNullOrderByStartTimeDesc(Long userId);
}
