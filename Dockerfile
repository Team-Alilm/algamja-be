FROM openjdk:17

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} alilm.jar

ENV JASYPT_ALGORITHM=$JASYPT_ALGORITHM
ENV JASYPT_PASSWORD=$JASYPT_PASSWORD

ENTRYPOINT ["java", "-Dspring.profiles.active=local" ,"-jar", "alilm.jar", "--build-arg JASYPT_ALGORITHM=$JASYPT_ALGORITHM", "--build-arg JASYPT_PASSWORD=$JASYPT_PASSWORD"]