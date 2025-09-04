#!/bin/bash

# EC2에서 Selenium Chrome 환경 설정 스크립트
# 사용법: sudo ./setup-ec2-selenium.sh

set -e

echo "🚀 Starting EC2 Selenium Chrome setup..."

# 시스템 업데이트
echo "📦 Updating system packages..."
apt-get update && apt-get upgrade -y

# 필수 패키지 설치
echo "📦 Installing essential packages..."
apt-get install -y \
    wget \
    curl \
    unzip \
    software-properties-common \
    apt-transport-https \
    ca-certificates \
    gnupg \
    lsb-release \
    xvfb \
    fonts-liberation \
    fonts-noto-cjk \
    fonts-noto-color-emoji

# Google Chrome 설치
echo "🌐 Installing Google Chrome..."
wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add -
echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list
apt-get update
apt-get install -y google-chrome-stable

# ChromeDriver 설치
echo "🚗 Installing ChromeDriver..."
CHROME_VERSION=$(google-chrome --version | awk '{print $3}' | cut -d. -f1)
echo "Chrome version: $CHROME_VERSION"

# ChromeDriver 최신 버전 확인 및 다운로드
CHROMEDRIVER_VERSION=$(curl -sS chromedriver.storage.googleapis.com/LATEST_RELEASE_$CHROME_VERSION)
echo "ChromeDriver version: $CHROMEDRIVER_VERSION"

wget -N http://chromedriver.storage.googleapis.com/$CHROMEDRIVER_VERSION/chromedriver_linux64.zip -P /tmp/
unzip -o /tmp/chromedriver_linux64.zip -d /tmp/
mv /tmp/chromedriver /usr/local/bin/chromedriver
chmod +x /usr/local/bin/chromedriver
rm /tmp/chromedriver_linux64.zip

# Java 17 설치 (Spring Boot용)
echo "☕ Installing Java 17..."
apt-get install -y openjdk-17-jdk

# Xvfb 서비스 설정
echo "🖥️  Setting up Xvfb service..."
cat > /etc/systemd/system/xvfb.service << EOF
[Unit]
Description=X Virtual Frame Buffer Service
After=network.target

[Service]
ExecStart=/usr/bin/Xvfb :99 -screen 0 1920x1080x24 -ac +extension GLX +render -noreset
ExecStop=/usr/bin/killall Xvfb
Restart=always
RestartSec=1

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable xvfb
systemctl start xvfb

# 환경 변수 설정
echo "🔧 Setting up environment variables..."
cat >> /etc/environment << EOF
DISPLAY=:99
CHROME_BIN=/usr/bin/google-chrome
CHROMEDRIVER_PATH=/usr/local/bin/chromedriver
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
EOF

# Chrome 테스트
echo "🧪 Testing Chrome installation..."
export DISPLAY=:99
google-chrome --version
chromedriver --version

# 권한 설정
echo "🔐 Setting up permissions..."
chmod -R 755 /usr/local/bin/chromedriver
chown root:root /usr/local/bin/chromedriver

# 로그 디렉토리 생성
mkdir -p /var/log/selenium
chmod 755 /var/log/selenium

echo "✅ EC2 Selenium Chrome setup completed!"
echo ""
echo "설정 완료 정보:"
echo "- Google Chrome: $(google-chrome --version)"
echo "- ChromeDriver: $(chromedriver --version)"
echo "- Java: $(java -version 2>&1 | head -n 1)"
echo "- Xvfb Display: :99 (1920x1080x24)"
echo ""
echo "다음 명령어로 서비스 상태를 확인할 수 있습니다:"
echo "sudo systemctl status xvfb"
echo ""
echo "애플리케이션 실행 시 다음 환경 변수가 설정됩니다:"
echo "export DISPLAY=:99"