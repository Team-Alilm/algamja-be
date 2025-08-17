# syntax=docker/dockerfile:1

############################
# Build stage (Java 21)
############################
FROM gradle:8-jdk21 AS builder
WORKDIR /app

COPY gradle gradle
COPY gradlew settings.gradle* build.gradle* ./
RUN chmod +x gradlew
COPY src src

RUN ./gradlew clean bootJar -x test --no-daemon

############################
# Runtime stage (Java 21)
############################
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar /app/app.jar
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]