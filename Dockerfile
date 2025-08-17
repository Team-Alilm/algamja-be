# syntax=docker/dockerfile:1

# 1) Build
FROM gradle:8-jdk-21 AS builder
WORKDIR /app
COPY gradle gradle
COPY gradlew settings.gradle* build.gradle* ./
COPY src src
RUN ./gradlew clean bootJar -x test

# 2) Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]