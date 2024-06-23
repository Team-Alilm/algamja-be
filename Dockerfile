FROM --platform=linux/x86_64 openjdk:17-jdk-alpine:3.14
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]