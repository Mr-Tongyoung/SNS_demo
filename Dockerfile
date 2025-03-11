FROM ubuntu:latest
LABEL authors="junhokim"

# OpenJDK 17 기반 이미지 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY build/libs/SNS_demo-0.0.1-SNAPSHOT.jar app.jar

# 실행 명령어 설정 (Docker 프로파일 활성화)
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]

