val exposedVersion: String by project
val slackVersion: String by project
val jasyptVersion: String by project
val jsoupVersion: String by project
val firebaseAdminVersion: String by project

dependencies {
    // ✅ 내부 모듈
    implementation(project(":domain"))

    // ✅ Spring Framework
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // ✅ 보안 · 암호화
    implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:$jasyptVersion")

    // ✅ ORM / Database 접근 (Exposed)
    implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.61.0")

    // ✅ Database 드라이버
    runtimeOnly("com.h2database:h2") // 개발/테스트용
    runtimeOnly("com.mysql:mysql-connector-j") // 운영용

    // ✅ 외부 서비스 연동
    implementation("com.google.firebase:firebase-admin:$firebaseAdminVersion") // Firebase Admin SDK
    implementation("com.slack.api:slack-api-client:$slackVersion")             // Slack API

    // ✅ 유틸리티
    implementation("org.jsoup:jsoup:$jsoupVersion") // HTML 파싱
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}