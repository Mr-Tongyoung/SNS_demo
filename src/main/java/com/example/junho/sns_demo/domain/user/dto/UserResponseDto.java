package com.example.junho.sns_demo.domain.user.dto;

import com.example.junho.sns_demo.global.util.Gender;
import java.time.LocalDate;

public record UserResponseDto(
    Long id,
    String username,
    String email,
    String role) {

}
