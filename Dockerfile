# syntax=docker/dockerfile:1

############################
# Build stage (Java 21)
############################
FROM gradle:8-jdk21 AS builder
WORKDIR /app

# Firebase key를 빌드 시 argument로 받음
ARG FIREBASE_KEY_B64

COPY gradle gradle
COPY gradlew settings.gradle* build.gradle* ./
RUN chmod +x gradlew
COPY src src

# Firebase 키 파일 생성 (빌드 시점에)
RUN if [ -n "$FIREBASE_KEY_B64" ]; then \
      mkdir -p src/main/resources/firebase && \
      echo "$FIREBASE_KEY_B64" | base64 -d > src/main/resources/firebase/FirebaseSecretKey.json; \
    fi

RUN ./gradlew clean bootJar -x test --no-daemon

############################
# Runtime stage (Java 21)
############################
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar /app/app.jar
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]