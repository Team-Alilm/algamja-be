FROM --platform=linux/x86_64 openjdk:21-jdk-alpine3.14

ARG JAR_FILE=/build/libs/alilm-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar", "--spring.profiles.active=prod"]