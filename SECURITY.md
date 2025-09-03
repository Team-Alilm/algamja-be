# Security Guidelines

## 환경 변수 설정

애플리케이션 실행 시 다음 환경 변수가 필요합니다:

```bash
JASYPT_PASSWORD=<your-jasypt-password>
JASYPT_ALGORITHM=PBEWithMD5AndDES
```

## Jasypt 암호화

민감한 정보(비밀번호, API 키 등)는 반드시 Jasypt로 암호화하여 저장해야 합니다.

### 암호화 방법

1. 환경 변수 설정
```bash
export JASYPT_PASSWORD='your-jasypt-password'
export JASYPT_ALGORITHM='PBEWithMD5AndDES'
```

2. JasyptEncryptUtil 사용
```bash
# Kotlin 클래스 직접 실행
kotlin -cp build/classes/kotlin/main org.team_alilm.algamja.common.util.JasyptEncryptUtil "text-to-encrypt"
```

3. application.yml에 적용
```yaml
spring:
  mail:
    username: ENC(암호화된값)
    password: ENC(암호화된값)
```

## 보안 주의사항

### 절대 하지 말아야 할 것들

1. **비밀번호 하드코딩 금지**
   - 소스 코드에 실제 비밀번호를 직접 작성하지 마세요
   - 테스트 코드에도 실제 비밀번호를 포함시키지 마세요

2. **환경 변수 노출 금지**
   - JASYPT_PASSWORD를 코드나 설정 파일에 포함시키지 마세요
   - .env 파일을 git에 커밋하지 마세요

3. **민감 정보 로깅 금지**
   - 비밀번호, API 키, 토큰 등을 로그에 출력하지 마세요

### 권장 사항

1. **환경별 분리**
   - 개발/스테이징/프로덕션 환경별로 다른 암호화 키 사용
   - application-{profile}.yml 파일로 환경별 설정 분리

2. **시크릿 관리**
   - AWS Secrets Manager, HashiCorp Vault 등 시크릿 관리 도구 사용 고려
   - CI/CD 파이프라인에서는 GitHub Secrets 또는 환경 변수 사용

3. **정기적인 키 교체**
   - 주기적으로 암호화 키 변경
   - 노출 의심 시 즉시 키 교체

## 이메일 서비스 보안

Gmail SMTP 사용 시:
- 2단계 인증 활성화
- 앱 비밀번호 사용 (일반 계정 비밀번호 사용 금지)
- 암호화된 연결(STARTTLS) 사용

## 문제 발생 시

보안 이슈 발견 시:
1. 즉시 노출된 키/비밀번호 변경
2. 영향 범위 파악
3. 팀에 공유 및 대응 방안 수립