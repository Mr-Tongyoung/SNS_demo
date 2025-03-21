package com.example.junho.sns_demo.domain.user.repository;

import com.example.junho.sns_demo.domain.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);

  Optional<User> findById(Long userId);

  User findByUsername(String username);

  @Query("SELECT u.id FROM User u")  // ✅ JPQL 사용
  List<Long> findAllUserIds();

  List<User> findAllByIdBetween(Long startId, Long endId);


}
