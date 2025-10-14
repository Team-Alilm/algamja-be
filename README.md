# 🥔 알감자 (Algamja) - 백엔드 프로젝트 문서

## 📋 목차
1. [프로젝트 개요](#프로젝트-개요)
2. [아키텍처](#아키텍처)
3. [기술 스택](#기술-스택)
4. [도메인 모델](#도메인-모델)
5. [데이터베이스 스키마](#데이터베이스-스키마)
6. [API 엔드포인트](#api-엔드포인트)
7. [개발 환경 설정](#개발-환경-설정)
8. [배포 및 운영](#배포-및-운영)
9. [주요 기능 상세](#주요-기능-상세)
10. [개발 가이드](#개발-가이드)

---

## 프로젝트 개요

### 서비스 소개
**알감자**는 무신사, 지그재그, 29cm와 같은 인기 온라인 쇼핑몰에서 **품절된 상품의 재입고 및 가격 변동**을 자동으로 추적하여 사용자에게 알려주는 서비스입니다.

### 핵심 가치
- 사용자가 원하는 상품이 재입고되면 즉시 알림
- 상품 가격 변동 히스토리 추적
- 다양한 쇼핑몰 지원 (무신사, 지그재그, 29cm)

### 주요 특징
- 옵션별(size, color 등) 세밀한 알림 설정
- 실시간 알림 (FCM 기반)
- 장바구니 기능으로 관심 상품 관리

### 프로젝트 정보
- **버전**: 0.0.1
- **서비스 URL**: https://algamja.com/
- **개발팀**: @cloudwi
- **문의**: cloudwi@naver.com

---

## 아키텍처

### 시스템 아키텍처

```
┌─────────────┐         ┌──────────────┐         ┌─────────────────┐
│   Client    │────────▶│  Spring Boot │────────▶│     MySQL       │
│  (Web/App)  │         │   Backend    │         │   Database      │
└─────────────┘         └──────────────┘         └─────────────────┘
                               │
                               │
                    ┌──────────┼──────────┐
                    │          │          │
              ┌─────▼────┐  ┌──▼───┐  ┌──▼──────┐
              │  E-Shop  │  │ FCM  │  │  Email  │
              │ Crawlers │  │      │  │ Service │
              └──────────┘  └──────┘  └─────────┘
```

### 레이어 구조

```
┌─────────────────────────────────────┐
│     Controller Layer                │  ← REST API 엔드포인트
├─────────────────────────────────────┤
│     Service Layer                   │  ← 비즈니스 로직
├─────────────────────────────────────┤
│     Repository Layer (Exposed)      │  ← 데이터 액세스
├─────────────────────────────────────┤
│     Domain/Entity Layer             │  ← 도메인 모델
└─────────────────────────────────────┘
```

### 주요 모듈

| 모듈 | 패키지 | 설명 |
|------|--------|------|
| **Member** | `org.team_alilm.algamja.member` | 회원 관리 (OAuth2 소셜 로그인) |
| **Product** | `org.team_alilm.algamja.product` | 상품 정보 관리 및 크롤링 |
| **Basket** | `org.team_alilm.algamja.basket` | 장바구니 (관심 상품 관리) |
| **Notification** | `org.team_alilm.algamja.notification` | 알림 관리 |
| **FCM** | `org.team_alilm.algamja.fcm` | Firebase Cloud Messaging |
| **Email** | `org.team_alilm.algamja.email` | 이메일 알림 |
| **Banner** | `org.team_alilm.algamja.banner` | 배너 관리 |

---

## 기술 스택

### Backend
| 분야 | 기술 | 버전 | 용도 |
|------|------|------|------|
| **Language** | Kotlin | 2.2.0 | 주 개발 언어 |
| **Framework** | Spring Boot | 3.x | 애플리케이션 프레임워크 |
| **JVM** | Java | 21 | 런타임 환경 |

### Database & ORM
| 기술 | 버전 | 용도 |
|------|------|------|
| **MySQL** | 8.x | 메인 데이터베이스 |
| **Exposed** | - | Kotlin DSL 기반 ORM |
| **Flyway** | - | DB 마이그레이션 관리 |
| **H2** | - | 테스트용 인메모리 DB |

### Security & Auth
| 기술 | 용도 |
|------|------|
| **Spring Security** | 인증/인가 |
| **OAuth2 Client** | 소셜 로그인 (구글, 카카오 등) |
| **JWT (jjwt)** | 토큰 기반 인증 |
| **Jasypt** | 민감 정보 암호화 |

### External Services
| 서비스 | 용도 |
|--------|------|
| **Firebase Admin** | FCM 푸시 알림 |
| **Spring Mail** | 이메일 발송 |
| **Jsoup** | HTML 파싱 (쇼핑몰 크롤링) |
| **Slack API** | 모니터링 알림 |

### Scheduling & Lock
| 기술 | 용도 |
|------|------|
| **ShedLock** | 분산 환경에서 스케줄러 중복 실행 방지 |
| **Spring Scheduler** | 주기적인 배치 작업 |

### Async & Coroutine
| 기술 | 용도 |
|------|------|
| **Kotlin Coroutines** | 비동기 처리 |
| **Reactor** | 리액티브 프로그래밍 |

### API Documentation
| 기술 | 용도 |
|------|------|
| **SpringDoc OpenAPI** | REST API 문서 자동 생성 (Swagger UI) |

### Build & CI/CD
| 기술 | 용도 |
|------|------|
| **Gradle (Kotlin DSL)** | 빌드 도구 |
| **Docker** | 컨테이너화 |
| **GitHub Actions** | CI/CD 파이프라인 |

### Testing
| 기술 | 용도 |
|------|------|
| **JUnit 5** | 테스트 프레임워크 |
| **Kotest** | Kotlin 테스트 라이브러리 |
| **MockK** | Kotlin 모킹 프레임워크 |
| **SpringMockK** | Spring + MockK 통합 |
| **Jacoco** | 코드 커버리지 |

### Monitoring & Operations
| 기술 | 용도 |
|------|------|
| **Spring Actuator** | 헬스 체크 및 메트릭 |
| **Logback** | 로깅 |
| **SonarQube** | 코드 품질 분석 |

### Infrastructure
| 서비스 | 용도 |
|--------|------|
| **AWS EC2** | 애플리케이션 서버 |
| **AWS RDS** | 관리형 MySQL 데이터베이스 |
| **AWS S3** | 정적 파일 저장소 |
| **Nginx** | 리버스 프록시 / 로드 밸런서 |

---

## 도메인 모델

### Member (회원)
```kotlin
- id: Long
- provider: String (GOOGLE, KAKAO, NAVER)
- providerId: String
- email: String
- nickname: String
- isDelete: Boolean
- createdDate: Long (timestamp)
- lastModifiedDate: Long
```

**관계**
- 1:N → Basket
- 1:N → Notification
- 1:N → FcmToken

---

### Product (상품)
```kotlin
- id: Long
- storeNumber: Long (쇼핑몰 상품 고유번호)
- name: String
- brand: String
- thumbnailUrl: String
- store: String (MUSINSA, ZIGZAG, TWENTYNINE_CM)
- firstCategory: String
- secondCategory: String?
- price: BigDecimal
- firstOption: String? (예: 사이즈)
- secondOption: String? (예: 색상)
- thirdOption: String?
- isDelete: Boolean
- createdDate: Long
- lastModifiedDate: Long
```

**제약조건**
- `uk_product_options`: (store, storeNumber, firstOption, secondOption, thirdOption) 조합이 유일해야 함

**관계**
- 1:N → ProductImage
- 1:N → Basket
- 1:N → Notification
- 1:N → ProductPriceHistory

---

### Basket (장바구니)
```kotlin
- id: Long
- memberId: Long
- productId: Long
- isNotification: Boolean (알림 발송 여부)
- notificationDate: Long? (알림 발송 시각)
- isHidden: Boolean (숨김 처리)
- isDelete: Boolean
- createdDate: Long
- lastModifiedDate: Long
```

**제약조건**
- `uk_basket_member_product`: (memberId, productId) 조합이 유일
- `ck_basket_notification_date_required`: isNotification이 true면 notificationDate가 필수

**인덱스**
- `idx_basket_member_active`: (memberId, isDelete)
- `idx_basket_product_active`: (productId, isDelete)

---

### Notification (알림)
```kotlin
- id: Long
- productId: Long
- memberId: Long
- readYn: Boolean
- isDelete: Boolean
- createdDate: Long
- lastModifiedDate: Long
```

---

### FcmToken (FCM 토큰)
```kotlin
- id: Long
- token: String (512자, 유일)
- memberId: Long
- isActive: Boolean
- isDelete: Boolean
- createdDate: Long
- lastModifiedDate: Long
```

**제약조건**
- `uk_fcm_token_token`: token이 유일해야 함

---

### ProductImage (상품 이미지)
```kotlin
- id: Long
- imageUrl: String (512자, 유일)
- productId: Long
- imageOrder: Int (이미지 순서)
- isDelete: Boolean
- createdDate: Long
- lastModifiedDate: Long
```

**인덱스**
- `idx_product_image_product_id`: productId
- `idx_product_image_product_id_order`: (productId, imageOrder)

---

### ProductPriceHistory (상품 가격 히스토리)
```kotlin
- id: Long
- productId: Long
- price: BigDecimal
- recordedAt: Long (기록 시점 timestamp)
- createdAt: Long
- updatedAt: Long
```

**제약조건**
- `chk_product_price_history_price_non_negative`: price >= 0
- `fk_product_price_history_product`: productId는 product 테이블 참조 (ON DELETE CASCADE)

**인덱스**
- `idx_product_price_history_product_id`: productId
- `idx_product_price_history_product_time`: (productId, recordedAt)

**비고**
- 매일 스냅샷 방식으로 가격을 저장
- 가격 변동 히스토리를 시계열로 조회 가능

---

### Banner (배너)
```kotlin
- id: Long
- title: String
- imageUrl: String
- linkUrl: String
- displayOrder: Int
- isActive: Boolean
- isDelete: Boolean
- createdDate: Long
- lastModifiedDate: Long
```

---

## 데이터베이스 스키마

### ERD 다이어그램

```
┌─────────────┐         ┌──────────────┐         ┌────────────────┐
│   member    │◀───────┤    basket    │────────▶│    product     │
└─────────────┘         └──────────────┘         └────────────────┘
       │                                                  │
       │                                                  │
       │                                                  ├──────────┐
       │                                                  │          │
       ▼                                                  ▼          ▼
┌─────────────┐         ┌──────────────┐      ┌──────────────┐  ┌──────────────────────┐
│  fcm_token  │         │ notification │      │product_image │  │product_price_history │
└─────────────┘         └──────────────┘      └──────────────┘  └──────────────────────┘
```

### 마이그레이션 히스토리

| 버전 | 파일명 | 설명 |
|------|--------|------|
| V1 | `V1__init.sql` | 초기 테이블 생성 (member, product, basket, notification, fcm_token, product_image, price_history) |
| V2 | `V2__modify_product_image_unique_constraint.sql` | product_image 유니크 제약조건 수정 |
| V3 | `V3__add_product_availability_columns.sql` | 상품 재고 관련 컬럼 추가 |
| V5 | `V5__convert_categories_to_english.sql` | 카테고리명 영문 변환 |
| V6 | `V6__create_banner_table.sql` | 배너 테이블 생성 |
| V7 | `V7__insert_sample_banner.sql` | 샘플 배너 데이터 삽입 |
| V9 | `V9__create_product_price_history_table.sql` | 상품 가격 히스토리 테이블 생성 (스냅샷 방식) |
| V10 | `V10__drop_price_history_table.sql` | 기존 price_history 테이블 제거 (product_price_history로 통합) |

### 인덱스 전략

**Member**
- PK: `id`
- UK: `(provider, provider_id)`

**Product**
- PK: `id`
- UK: `(store, store_number, first_option, second_option, third_option)`

**Basket**
- PK: `id`
- UK: `(member_id, product_id)`
- IDX: `(member_id, is_delete)`, `(product_id, is_delete)`

**ProductImage**
- PK: `id`
- UK: `image_url`
- IDX: `product_id`, `(product_id, image_order)`

**ProductPriceHistory**
- PK: `id`
- IDX: `product_id`, `(product_id, recorded_at)`

---

## API 엔드포인트

### Base URL
- **Production**: `https://api.algamja.com`
- **Local**: `http://localhost:8080`

### API Documentation
- **Swagger UI**: `{BASE_URL}/swagger-ui.html`
- **OpenAPI Spec**: `{BASE_URL}/v3/api-docs`

### 주요 엔드포인트

#### 1. Member (회원)
```
GET    /api/v1/members/me           # 내 정보 조회
PUT    /api/v1/members/me           # 내 정보 수정
DELETE /api/v1/members/me           # 회원 탈퇴
```

#### 2. Basket (장바구니)
```
GET    /api/v1/baskets              # 내 장바구니 목록
POST   /api/v1/baskets              # 장바구니 추가
DELETE /api/v1/baskets/{id}         # 장바구니 삭제
PATCH  /api/v1/baskets/{id}/hide    # 장바구니 숨김
```

#### 3. Product (상품)
```
POST   /api/v1/products/parse       # URL로 상품 파싱
GET    /api/v1/products/{id}        # 상품 상세 조회
GET    /api/v1/products/{id}/price-history  # 가격 히스토리 조회
```

#### 4. Notification (알림)
```
GET    /api/v1/notifications         # 내 알림 목록
PATCH  /api/v1/notifications/{id}/read  # 알림 읽음 처리
DELETE /api/v1/notifications/{id}    # 알림 삭제
```

#### 5. FCM Token
```
POST   /api/v1/fcm/tokens           # FCM 토큰 등록
DELETE /api/v1/fcm/tokens           # FCM 토큰 삭제
```

#### 6. Banner
```
GET    /api/v1/banners              # 활성 배너 목록
```

#### 7. Health Check
```
GET    /actuator/health             # 헬스 체크
GET    /actuator/info               # 애플리케이션 정보
```

---

## 개발 환경 설정

### 필수 사전 설치

1. **JDK 21**
   ```bash
   # Mac (Homebrew)
   brew install openjdk@21

   # 또는 SDKMAN 사용
   sdk install java 21-open
   ```

2. **Kotlin 2.2.0** (자동 설치됨)

3. **Docker & Docker Compose**
   ```bash
   # Mac (Homebrew)
   brew install docker docker-compose
   ```

### 로컬 환경 구성

#### 1. 저장소 클론
```bash
git clone https://github.com/your-org/algamja-be.git
cd algamja-be
```

#### 2. Docker로 인프라 실행
```bash
# MySQL, Redis 등 실행
docker-compose -f docker-compose-local.yml up -d
```

#### 3. 환경변수 설정
`src/main/resources/application-local.yml` 파일 생성:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/algamja?serverTimezone=Asia/Seoul
    username: algamja
    password: algamja123

  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_GOOGLE_CLIENT_ID
            client-secret: YOUR_GOOGLE_CLIENT_SECRET
          kakao:
            client-id: YOUR_KAKAO_CLIENT_ID
            client-secret: YOUR_KAKAO_CLIENT_SECRET

jwt:
  secret: YOUR_JWT_SECRET_KEY_HERE

firebase:
  credentials-path: src/main/resources/firebase/FirebaseSecretKey.json
```

#### 4. Firebase 설정
Firebase Admin SDK 키 파일을 다운로드하여 `src/main/resources/firebase/FirebaseSecretKey.json`에 저장

#### 5. 애플리케이션 실행
```bash
# Gradle로 실행
./gradlew bootRun

# 또는 IDE에서 AlgamjaApplication.kt 실행
```

### 데이터베이스 초기화

Flyway가 자동으로 마이그레이션을 실행합니다.
- 마이그레이션 파일: `src/main/resources/db/migration/V*.sql`
- 스키마: `algamja`

수동 초기화가 필요한 경우:
```bash
./gradlew flywayMigrate
```

### 테스트 실행

```bash
# 전체 테스트
./gradlew test

# 커버리지 리포트 생성
./gradlew jacocoTestReport

# 커버리지 검증
./gradlew jacocoTestCoverageVerification
```

### 코드 품질 검사

```bash
# SonarQube 분석 (로컬 SonarQube 필요)
./gradlew sonar
```

---

## 배포 및 운영

### Docker 빌드

```bash
# 이미지 빌드
docker build -t algamja-backend:latest .

# 컨테이너 실행
docker run -d \
  --name algamja-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  algamja-backend:latest
```

### 프로덕션 배포 (Docker Compose)

```bash
# 프로덕션 환경 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f backend

# 중지
docker-compose down
```

### 환경별 설정 파일

| 환경 | 프로파일 | 설정 파일 |
|------|----------|-----------|
| **로컬 개발** | `local` | `application-local.yml` |
| **테스트** | `test` | `application-test.yml` |
| **프로덕션** | `prod` | `application-prod.yml` |

### 프로파일 전환

```bash
# 로컬 환경
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun

# 프로덕션 환경
SPRING_PROFILES_ACTIVE=prod java -jar build/libs/app.jar
```

### 모니터링

#### Actuator 엔드포인트
- **Health**: `/actuator/health`
- **Info**: `/actuator/info`
- **Metrics**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus`

#### 로깅
- 로그 파일 위치: `logs/application.log`
- 로그 롤링: 일별, 압축 저장
- 설정 파일: `src/main/resources/logback-spring.xml`

#### Slack 알림
- 주요 오류 발생 시 Slack으로 알림
- 스케줄러 실행 결과 통지

---

## 주요 기능 상세

### 1. 상품 크롤링 및 파싱

#### 지원 쇼핑몰
- **무신사 (MUSINSA)**: `https://www.musinsa.com/`
- **지그재그 (ZIGZAG)**: `https://zigzag.kr/`
- **29cm**: `https://www.29cm.co.kr/`

#### 크롤링 정보
- 상품명, 브랜드, 가격
- 카테고리 (1차, 2차)
- 옵션 (사이즈, 색상 등)
- 이미지 URL
- 재고 상태

#### 크롤링 주기
- 스케줄러를 통해 주기적으로 실행
- ShedLock으로 중복 실행 방지

---

### 2. 재입고 알림

#### 알림 발송 조건
1. 사용자가 장바구니에 상품 추가
2. 해당 상품이 품절 상태에서 재입고로 변경
3. 옵션이 일치하는 경우

#### 알림 채널
- **FCM 푸시 알림** (앱/웹)
- **이메일 알림** (선택 사항)

#### 알림 발송 플로우
```
1. 스케줄러가 상품 재고 체크
2. 재입고 감지 시 해당 상품을 장바구니에 담은 사용자 조회
3. FCM 토큰이 활성화된 사용자에게 푸시 알림
4. Notification 레코드 생성
5. Basket의 isNotification = true, notificationDate 업데이트
```

---

### 3. 가격 변동 히스토리

#### 저장 방식
- **매일 스냅샷 방식**: 매일 정해진 시간에 상품 가격을 기록
- `product_price_history` 테이블에 저장

#### 히스토리 조회
- 특정 상품의 가격 추이를 시계열로 조회
- 그래프 형태로 프론트엔드에 제공

#### 활용
- 사용자에게 가격 변동 트렌드 제공
- 가격 하락 시 알림 (향후 기능)

---

### 4. OAuth2 소셜 로그인

#### 지원 프로바이더
- Google
- Kakao
- Naver (예정)

#### 인증 플로우
```
1. 클라이언트가 OAuth2 인증 요청
2. 프로바이더에서 인증 후 리다이렉트
3. 백엔드에서 프로바이더 정보로 회원 조회/생성
4. JWT 토큰 발급
5. 클라이언트는 JWT로 API 요청
```

#### JWT 토큰 구조
```json
{
  "sub": "member_id",
  "email": "user@example.com",
  "provider": "GOOGLE",
  "exp": 1234567890
}
```

---

### 5. 장바구니 관리

#### 주요 기능
- 관심 상품 등록
- 알림 발송 여부 추적
- 숨김 처리 (삭제하지 않고 목록에서 제외)

#### 비즈니스 규칙
- 동일 상품은 1번만 등록 가능 (`uk_basket_member_product`)
- 알림 발송 후 `isNotification=true`, `notificationDate` 기록
- `isHidden=true`인 항목은 목록에서 제외

---

## 개발 가이드

### 코드 스타일

#### Kotlin Convention
- **Naming**: camelCase (변수, 함수), PascalCase (클래스)
- **Nullable**: 가능한 Non-null 타입 사용, 필요 시 `?` 명시
- **Data Class**: Entity가 아닌 DTO는 data class 사용
- **함수형 프로그래밍**: map, filter, let, apply 등 적극 활용

#### 패키지 구조
```
org.team_alilm.algamja
├── common/              # 공통 유틸리티, 예외 처리
├── member/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── domain/          # Entity (Exposed Table)
│   └── dto/
├── product/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── domain/
│   ├── dto/
│   └── price/           # 가격 히스토리 서브 모듈
├── basket/
├── notification/
├── fcm/
└── email/
```

---

### 테스트 작성 가이드

#### 테스트 구조
```kotlin
@SpringBootTest
class ServiceTest {
    @Autowired
    lateinit var service: SomeService

    @MockkBean
    lateinit var repository: SomeRepository

    @Test
    fun `테스트 시나리오 설명`() {
        // Given
        every { repository.findById(any()) } returns mockData

        // When
        val result = service.doSomething(1L)

        // Then
        result shouldBe expectedValue
    }
}
```

#### 커버리지 목표
- **전체**: 10% 이상 (현실적인 목표)
- 주요 비즈니스 로직은 최대한 테스트 작성

---

### Git 브랜치 전략

#### Branch 규칙
- `main`: 프로덕션 배포 브랜치
- `develop`: 개발 통합 브랜치
- `feature/{기능명}`: 기능 개발
- `fix/{버그명}`: 버그 수정
- `refactor/{리팩토링명}`: 리팩토링

#### Commit Message Convention
```
feat: 새로운 기능 추가
fix: 버그 수정
refactor: 코드 리팩토링
docs: 문서 수정
test: 테스트 추가/수정
chore: 빌드 설정, 패키지 매니저 등
```

예시:
```bash
git commit -m "feat: 가격 히스토리 저장 방식을 매일 스냅샷 방식으로 변경"
git commit -m "fix: Flyway validation 비활성화로 실패한 마이그레이션 복구"
```

---

### CI/CD 파이프라인

#### GitHub Actions Workflow
```yaml
1. 코드 체크아웃
2. JDK 21 설정
3. Gradle 빌드 및 테스트
4. Jacoco 커버리지 리포트 생성
5. Docker 이미지 빌드
6. AWS ECR 푸시
7. EC2 배포
```

#### 배포 자동화
- `main` 브랜치에 push 시 자동 배포
- 테스트 실패 시 배포 중단

---

### 보안 가이드

#### 민감 정보 관리
- **Jasypt**: 설정 파일의 비밀번호 암호화
- **환경변수**: OAuth2 클라이언트 시크릿, JWT 키 등
- **.gitignore**: Firebase 키 파일, 로컬 설정 파일 제외

#### API 보안
- **JWT 인증**: Bearer Token 방식
- **CORS**: 허용된 Origin만 접근
- **Rate Limiting**: (향후 도입 예정)

---

### 성능 최적화

#### 데이터베이스
- **인덱스**: 자주 조회하는 컬럼에 인덱스 추가
- **N+1 문제**: Exposed DSL에서 join 사용
- **Connection Pool**: HikariCP 기본 설정 최적화

#### 캐싱 전략
- (향후 도입 예정) Redis 캐싱

#### 비동기 처리
- **Kotlin Coroutines**: I/O 작업 비동기 처리
- **스케줄러**: 백그라운드 작업 분리

---

## FAQ

### Q1. 로컬 환경에서 OAuth2 로그인이 안 돼요
**A**: `application-local.yml`에 Google/Kakao 클라이언트 ID/Secret이 올바르게 설정되었는지 확인하세요. 또한 OAuth2 Redirect URI가 `http://localhost:8080/login/oauth2/code/{provider}`로 등록되어 있어야 합니다.

### Q2. Flyway 마이그레이션이 실패해요
**A**: 현재 설정에서는 `flyway.validateOnMigrate=false`로 되어 있습니다. 만약 마이그레이션 파일을 수정했다면, checksum 불일치로 실패할 수 있습니다. `flyway_schema_history` 테이블을 확인하고 필요 시 수동으로 정리하세요.

### Q3. 테스트 실행 시 메모리 부족 오류가 발생해요
**A**: `build.gradle.kts`에서 테스트 JVM 옵션이 `-Xmx3g`로 설정되어 있습니다. 메모리가 부족한 경우 이 값을 줄이거나, 병렬 실행 수(`maxParallelForks`)를 조정하세요.

### Q4. Firebase FCM 푸시 알림이 안 가요
**A**: Firebase Admin SDK 키 파일(`FirebaseSecretKey.json`)이 올바른 경로에 있는지, 그리고 FCM 토큰이 유효한지 확인하세요.

### Q5. 가격 히스토리가 저장되지 않아요
**A**: 가격 히스토리는 스케줄러를 통해 매일 정해진 시간에 저장됩니다. 로컬 환경에서는 스케줄러가 비활성화되어 있을 수 있으니 `@Scheduled` 어노테이션이 활성화되어 있는지 확인하세요.

---

## 참고 자료

### 공식 문서
- [Kotlin 공식 문서](https://kotlinlang.org/docs/home.html)
- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Exposed ORM 문서](https://github.com/JetBrains/Exposed)
- [Flyway 문서](https://flywaydb.org/documentation/)

### 프로젝트 관련
- **GitHub**: https://github.com/your-org/algamja-be
- **Swagger UI**: https://api.algamja.com/swagger-ui.html (프로덕션)
- **서비스 URL**: https://algamja.com/

### 연락처
- **이메일**: cloudwi@naver.com
- **슬랙**: (프로젝트 슬랙 채널 URL)

---

## 변경 이력

| 날짜 | 버전 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 2025-01-15 | 1.0.0 | 초기 문서 작성 | Claude |
| 2025-01-XX | 1.1.0 | 가격 히스토리 스냅샷 방식으로 변경 | @cloudwi |

---

**문서 작성**: Claude Code
**최종 업데이트**: 2025-01-15
**문서 버전**: 1.0.0
