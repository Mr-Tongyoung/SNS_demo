package com.example.junho.sns_demo.domain.user.dto;

import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.global.util.Gender;
import java.time.LocalDate;

public record UserRequestDto(
    String name,
    String loginId,
    String password,
    LocalDate birthday,
    String nickname,
    Gender gender,
    String phone,
    String email
) {
  public User toEntity() {
    return User.builder()
        .name(this.name())
        .loginId(this.loginId())
        .password(this.password())
        .birthday(this.birthday())
        .nickname(this.nickname())
        .gender(this.gender())
        .phone(this.phone())
        .email(this.email())
        .build();
  }
}
