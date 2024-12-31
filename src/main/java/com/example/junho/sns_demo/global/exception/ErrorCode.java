package com.example.junho.sns_demo.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  // Global

  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다."),

  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

  ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),

  POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."),


  // Member
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원이 존재하지 않습니다."),

  LOGIN_ID_ALREADY_EXISTS(HttpStatus.INTERNAL_SERVER_ERROR, "이미 존재하는 아이디입니다"),

  EMAIL_ALREADY_EXISTS(HttpStatus.INTERNAL_SERVER_ERROR, "이미 존재하는 이메일입니다"),

  PHONE_ALREADY_EXISTS(HttpStatus.INTERNAL_SERVER_ERROR, "이미 존재하는 전화번호입니다"),

  NICKNAME_ALREADY_EXISTS(HttpStatus.INTERNAL_SERVER_ERROR, "이미 존재하는 닉네임입니다"),

  PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다."),

  // Comment
  COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."),

  COMMENT_NOT_OWNED_BY_USER(HttpStatus.FORBIDDEN, "댓글의 작성자가 아닙니다.");

  private final HttpStatus status;
  private final String message;
}