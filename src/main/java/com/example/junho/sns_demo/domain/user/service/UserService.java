package com.example.junho.sns_demo.domain.user.service;

import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.domain.user.dto.UserRegisterDto;
import com.example.junho.sns_demo.domain.user.dto.UserResponseDto;
import com.example.junho.sns_demo.domain.user.repository.UserRepository;
import com.example.junho.sns_demo.global.exception.CustomException;
import com.example.junho.sns_demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder bCryptPasswordEncoder;

  // 회원가입
  public UserResponseDto registerUser(UserRegisterDto userRegisterDto) {

    checkDuplication(userRegisterDto);

    User user = userRegisterDto.toEntity();
    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword())); // 비밀번호 해싱
    user.setRole("ROLE_USER");

    return userRepository.save(user).toResponseDto();
  }

  private void checkDuplication(UserRegisterDto userRegisterDto) {
    if(userRepository.existsByEmail(userRegisterDto.email()))
      throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
  }


}
