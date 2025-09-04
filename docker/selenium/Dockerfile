# EC2용 Selenium Chrome Dockerfile
FROM selenoid/vnc:chrome_120.0

USER root

# 필수 패키지 설치
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    unzip \
    xvfb \
    fonts-liberation \
    fonts-noto-cjk \
    fonts-noto-color-emoji \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Chrome과 ChromeDriver가 이미 설치되어 있음을 확인
RUN google-chrome --version && chromedriver --version

# Xvfb 설정 스크립트 생성
RUN echo '#!/bin/bash\n\
export DISPLAY=:99\n\
Xvfb :99 -screen 0 1920x1080x24 &\n\
exec "$@"' > /usr/local/bin/entrypoint.sh && \
    chmod +x /usr/local/bin/entrypoint.sh

# 한국어 폰트 설정
RUN echo 'ko_KR.UTF-8 UTF-8' >> /etc/locale.gen && \
    locale-gen

ENV LANG=ko_KR.UTF-8
ENV LANGUAGE=ko_KR:ko
ENV LC_ALL=ko_KR.UTF-8

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
CMD ["google-chrome"]