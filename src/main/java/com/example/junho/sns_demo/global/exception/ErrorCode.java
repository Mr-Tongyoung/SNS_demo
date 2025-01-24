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


  // USER
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원이 존재하지 않습니다."),

  LOGIN_ID_ALREADY_EXISTS(HttpStatus.INTERNAL_SERVER_ERROR, "이미 존재하는 아이디입니다"),

  EMAIL_ALREADY_EXISTS(HttpStatus.INTERNAL_SERVER_ERROR, "이미 존재하는 이메일입니다"),

  PHONE_ALREADY_EXISTS(HttpStatus.INTERNAL_SERVER_ERROR, "이미 존재하는 전화번호입니다"),

  NICKNAME_ALREADY_EXISTS(HttpStatus.INTERNAL_SERVER_ERROR, "이미 존재하는 닉네임입니다"),

  PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다."),

  // Follow
  CANNOT_FOLLOW_MYSELF(HttpStatus.BAD_REQUEST, "자신은 팔로우 할 수 없습니다."),

  ALREADY_FOLLOWING(HttpStatus.BAD_REQUEST, "이미 팔로우 하고 있습니다."),

  NO_SUCH_FOLLOWERSHIP(HttpStatus.BAD_REQUEST, "팔로우 관계가 없습니다."),

  //Post
  POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."),

  POST_NOT_OWNED_BY_USER(HttpStatus.FORBIDDEN, "게시글의 작성자가 아닙니다."),

  //Like
  NO_SUCH_LIKE(HttpStatus.BAD_REQUEST, "좋아요 관계가 없습니다."),

  ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 좋아요한 게시물입니다."),

  ALREADY_UNLIKED(HttpStatus.BAD_REQUEST, "이미 좋아요를 취소한 게시물입니다."),

  // Comment
  COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."),

  COMMENT_NOT_OWNED_BY_USER(HttpStatus.FORBIDDEN, "댓글의 작성자가 아닙니다."),

  //S3
  FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload media file");

  private final HttpStatus status;
  private final String message;
}