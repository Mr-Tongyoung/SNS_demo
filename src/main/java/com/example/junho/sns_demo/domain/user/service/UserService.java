package com.example.junho.sns_demo.domain.user.service;

import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.domain.user.dto.UserRequestDto;
import com.example.junho.sns_demo.domain.user.dto.UserResponseDto;
import com.example.junho.sns_demo.domain.user.repository.UserRepository;
import com.example.junho.sns_demo.global.exception.CustomException;
import com.example.junho.sns_demo.global.exception.ErrorCode;
import lombok.Builder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Builder
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder bCryptPasswordEncoder;

  // 회원가입
  public UserResponseDto registerUser(UserRequestDto userRequestDto) {

    checkDuplication(userRequestDto);

    User user = userRequestDto.toEntity();
    String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
    user.setPassword(encodedPassword); // 비밀번호 해싱

    return userRepository.save(user).toResponseDto();
  }

  // 로그인
  public UserResponseDto loginUser(String loginId, String password) {
    // 로그인 ID로 사용자 조회
    User user = userRepository.findByLoginId(loginId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    // 비밀번호 검증
    if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
      throw new CustomException(ErrorCode.PASSWORD_INCORRECT);
    }

    return user.toResponseDto();
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
