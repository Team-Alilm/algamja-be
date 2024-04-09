#!/usr/bin/env bash

# 프로젝트 루트 디렉토리 및 JAR 파일 경로 설정
PROJECT_ROOT="/home/ec2-user/app/deploy"
JAR_FILE="$PROJECT_ROOT/build/libs/Alilm-Be-0.0.1-SNAPSHOT.jar"

# 로그 파일 경로 설정
APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

# 현재 시간 구하기
TIME_NOW=$(date +%c)

# build 파일 복사
echo "$TIME_NOW > $JAR_FILE 파일 복사" >> "$DEPLOY_LOG"
cp "$PROJECT_ROOT/build/libs/*.jar" "$JAR_FILE"

# 프로파일 설정 및 jar 파일 실행
echo "$TIME_NOW > $JAR_FILE 파일 실행 (프로파일: prod)" >> "$DEPLOY_LOG"
nohup java -Dspring.profiles.active=prod -jar "$JAR_FILE" > "$APP_LOG" 2> "$ERROR_LOG" &

# 실행된 프로세스 아이디 확인 및 로그에 기록
CURRENT_PID=$(pgrep -f "$JAR_FILE")
echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다." >> "$DEPLOY_LOG"
