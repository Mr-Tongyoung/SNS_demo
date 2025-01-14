package com.example.junho.sns_demo.domain.user.dto;

import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.global.util.Gender;
import java.time.LocalDate;

public record UserRegisterDto(
    String username,
    String password,
    String email
) {
  public User toEntity() {
    return User.builder()
        .username(username)
        .password(password)
        .email(email)
        .build();
  }
}
