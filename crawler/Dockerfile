FROM openjdk:21-jdk

# Chrome 및 ChromeDriver 설치
RUN apt-get update && apt-get install -y \
    google-chrome-stable \
    chromium-driver \
    && rm -rf /var/lib/apt/lists/*

# Chrome 버전 확인 (디버깅용)
RUN google-chrome --version
RUN chromedriver --version

ARG JAR_FILE=/build/libs/crawler-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar", "--spring.profiles.active=prod"]
