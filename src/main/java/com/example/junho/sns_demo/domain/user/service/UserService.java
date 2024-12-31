package com.example.junho.sns_demo.domain.user.service;

import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.domain.user.dto.UserRequestDto;
import com.example.junho.sns_demo.domain.user.dto.UserResponseDto;
import com.example.junho.sns_demo.domain.user.repository.UserRepository;
import com.example.junho.sns_demo.global.exception.CustomException;
import com.example.junho.sns_demo.global.exception.ErrorCode;
import lombok.Builder;
import org.springframework.stereotype.Service;

@Service
@Builder
public class UserService {

  private final UserRepository userRepository;

  public UserResponseDto registerUser(UserRequestDto userRequestDto) {

    checkDuplication(userRequestDto);

    User user = userRequestDto.toEntity();

    User savedUser = userRepository.save(user);

    return savedUser.toResponseDto();
  }

  private void checkDuplication(UserRequestDto userRequestDto) {
    if(userRepository.existsByLoginId(userRequestDto.loginId()))
      throw new CustomException(ErrorCode.LOGIN_ID_ALREADY_EXISTS);
    if(userRepository.existsByNickname(userRequestDto.nickname()))
      throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
    if(userRepository.existsByPhone(userRequestDto.phone()))
      throw new CustomException(ErrorCode.PHONE_ALREADY_EXISTS);
    if(userRepository.existsByEmail(userRequestDto.email()))
      throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
  }
}
