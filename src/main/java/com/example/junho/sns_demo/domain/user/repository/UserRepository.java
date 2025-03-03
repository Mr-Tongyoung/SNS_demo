package com.example.junho.sns_demo.domain.user.repository;

import com.example.junho.sns_demo.domain.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

  boolean existsByEmail(String email);

  Optional<User> findById(Long userId);

  User findByUsername(String username);
}
