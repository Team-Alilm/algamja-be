FROM ubuntu:22.04

# Java 설치
RUN apt-get update && \
    apt-get install -y openjdk-21-jdk wget gnupg unzip && \
    rm -rf /var/lib/apt/lists/*

ARG JAR_FILE=/build/libs/crawler-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar", "--spring.profiles.active=prod"]