# Ably 크롤러 EC2 환경 설정 가이드

## 개요

Ably 크롤러는 Cloudflare WAF 우회와 EC2 환경 안정성을 위해 하이브리드 구조로 설계되었습니다:

1. **1차 시도**: REST API 크롤링 (빠르고 효율적)
2. **2차 백업**: Selenium 웹 크롤링 (API 실패 시)

## 주요 개선사항

### 1. Cloudflare 우회 전략

#### API 크롤링 개선 (`AblyCrawler`)
- **다중 User-Agent 풀**: 4개의 실제 브라우저 User-Agent 순환 사용
- **향상된 HTTP 헤더**: 실제 브라우저와 동일한 헤더 세트 전송
- **지능형 재시도**: 실패 시 다른 User-Agent로 최대 3회 재시도
- **지연 시간 증가**: 재시도마다 지연 시간을 점진적으로 증가

```kotlin
// 헤더 예시
.header("Accept", "application/json, text/plain, */*")
.header("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8")
.header("Sec-Fetch-Dest", "empty")
.header("Sec-Fetch-Mode", "cors")
.header("Origin", "https://m.a-bly.com")
.header("Referer", "https://m.a-bly.com/")
```

#### Selenium 크롤링 (`AblySeleniumCrawler`)
- **Chrome DevTools 활용**: 네트워크 레벨 헤더 주입
- **브라우저 탐지 우회**: `--disable-blink-features=AutomationControlled` 등
- **환경 감지**: EC2에서는 자동으로 헤드리스 모드, 로컬에서는 GUI 모드
- **최적화된 설정**: 이미지/JS 비활성화로 속도 향상

### 2. EC2 환경 최적화

#### 시스템 요구사항
```bash
# 최소 요구사항
- RAM: 2GB 이상 (권장: 4GB)
- CPU: 2vCPU 이상
- 디스크: 10GB 여유 공간
- OS: Ubuntu 20.04 LTS 이상
```

#### 자동 설치 스크립트
```bash
# EC2 인스턴스에서 실행
sudo chmod +x scripts/setup-ec2-selenium.sh
sudo ./scripts/setup-ec2-selenium.sh
```

설치되는 구성 요소:
- Google Chrome (최신 안정 버전)
- ChromeDriver (Chrome 버전과 호환)
- Java 17 JDK
- Xvfb (가상 디스플레이 서버)
- 한국어 폰트 패키지

### 3. 하이브리드 크롤러 구조

#### `AblyHybridCrawler`
```kotlin
@Component
@Order(1) // 최우선 순위
@ConditionalOnProperty(name = "crawler.ably.fallback-to-selenium", havingValue = "true")
class AblyHybridCrawler
```

동작 흐름:
1. API 크롤링 시도 → 성공시 즉시 반환
2. API 실패시 → Selenium 백업 크롤링 시도
3. 양쪽 모두 실패시 → 예외 발생

## 설정 및 배포

### 1. application-ec2.yml 설정

```yaml
# EC2 환경 활성화
spring:
  profiles:
    active: ec2

# 크롤링 설정
crawler:
  ably:
    fallback-to-selenium: true
    max-retries: 3
    retry-delay-ms: 2000
    selenium-timeout-seconds: 30
```

### 2. EC2 배포 단계

1. **환경 설정**
```bash
# 1. 설치 스크립트 실행
sudo ./scripts/setup-ec2-selenium.sh

# 2. Xvfb 서비스 확인
sudo systemctl status xvfb

# 3. 환경 변수 확인
echo $DISPLAY  # :99 출력 확인
```

2. **애플리케이션 실행**
```bash
# 환경 변수 설정 후 실행
export DISPLAY=:99
java -jar -Dspring.profiles.active=ec2 alilm-be.jar
```

3. **Docker 사용 (선택사항)**
```bash
# Selenium Chrome 컨테이너 빌드
docker build -f docker/selenium/Dockerfile -t alilm-selenium .

# 컨테이너 실행
docker run -d --name selenium-chrome \
  -v /dev/shm:/dev/shm \
  --security-opt seccomp=unconfined \
  alilm-selenium
```

### 3. 로그 모니터링

```bash
# 크롤러 로그 확인
tail -f /var/log/alilm/crawler.log

# 실시간 크롤링 상태 모니터링
grep "hybrid crawling\|Primary API\|Fallback Selenium" /var/log/alilm/crawler.log
```

## 트러블슈팅

### 1. Chrome/ChromeDriver 버전 불일치
```bash
# Chrome 버전 확인
google-chrome --version

# ChromeDriver 재설치
CHROME_VERSION=$(google-chrome --version | awk '{print $3}' | cut -d. -f1)
wget -N http://chromedriver.storage.googleapis.com/LATEST_RELEASE_$CHROME_VERSION -O /tmp/version
CHROMEDRIVER_VERSION=$(cat /tmp/version)
# ... 다운로드 및 설치
```

### 2. Xvfb 디스플레이 문제
```bash
# Xvfb 서비스 재시작
sudo systemctl restart xvfb

# 디스플레이 수동 시작
Xvfb :99 -screen 0 1920x1080x24 &
export DISPLAY=:99
```

### 3. 메모리 부족 문제
```bash
# 메모리 사용량 모니터링
htop
free -h

# Chrome 프로세스 정리
pkill chrome
pkill chromedriver
```

### 4. Cloudflare 차단 지속 시
- User-Agent 풀을 더 다양하게 확장
- 요청 간격을 더 길게 설정 (`RETRY_DELAY_MS` 증가)
- 프록시 서버 사용 고려

## 성능 최적화 팁

1. **리소스 절약**
   - `--disable-images`, `--disable-javascript` 옵션 활용
   - 불필요한 Chrome 확장 프로그램 비활성화

2. **속도 향상**
   - API 크롤링을 우선적으로 사용 (Selenium은 백업용)
   - 페이지 로딩 대기 시간 최소화

3. **안정성 향상**
   - 재시도 로직과 지수 백오프 적용
   - 로그를 통한 실시간 모니터링

## 결과 검증

정상 동작 확인을 위한 테스트 URL:
```
https://m.a-bly.com/goods/27966224
```

예상 응답:
- 상품명: 추출 성공
- 가격: 숫자 형태로 추출
- 이미지: 1개 이상 URL 목록
- 옵션: 색상/사이즈 등 추출 (있는 경우)

로그에서 `✅ Primary API crawling successful` 또는 `✅ Fallback Selenium crawling successful` 메시지 확인