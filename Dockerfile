FROM openjdk:17

ARG JAR_FILE=build/libs/Alilm-Be-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} alilm.jar

ENTRYPOINT ["java","-jar","/alilm.jar"]