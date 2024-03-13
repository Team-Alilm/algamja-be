FROM openjdk:17

ARG JAR_FILE=build/libs/Alilm-Be-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} alilm.jar

ENV JASYPT_ALGORITHM=${JASYPT_ALGORITHM}
ENV JASYPT_PASSWORD=${JASYPT_PASSWORD}

ENTRYPOINT ["java", "-Dspring.profiles.active=prod" ,"-jar", "alilm.jar"]