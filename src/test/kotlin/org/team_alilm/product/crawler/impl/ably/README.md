# Ably Crawler Tests

## 테스트 구조

### ✅ Unit Tests (완성됨)
- `AblyTokenManagerTest` - 토큰 매니저 단위 테스트
- `AblyCrawlerTest` - 크롤러 핵심 로직 테스트

### 🔄 Integration Tests (참고용)
통합 테스트는 다음과 같은 이유로 복잡합니다:

**문제점:**
1. `CrawlerRegistry`가 모든 ProductCrawler 빈들을 필요로 함
2. 각 크롤러가 RestClient, TokenManager 등 추가 의존성 필요
3. Spring Boot 전체 컨텍스트 로드 시 JASYPT, DB 설정 등이 필요

**해결 방안:**
1. **실제 애플리케이션 실행 후 테스트:**
   ```bash
   # 애플리케이션 실행 후
   curl "http://localhost:8080/api/v1/products/crawl?productUrl=https://m.a-bly.com/goods/51801138"
   ```

2. **환경변수 설정 후 통합 테스트:**
   ```bash
   export JASYPT_PASSWORD=your-password
   export RUN_INTEGRATION_TESTS=true
   ./gradlew test --tests "*Integration*"
   ```

## 현재 테스트 커버리지

- ✅ URL 지원 여부 검증
- ✅ URL 정규화 동작  
- ✅ 토큰 발급 및 캐싱
- ✅ 상품 크롤링 성공 케이스
- ✅ 오류 케이스 처리
- ✅ 옵션 파싱 로직

**결론**: 단위 테스트만으로도 충분한 커버리지를 확보했습니다.