import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.Duration

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    java
    jacoco
    id("io.gitlab.arturbosch.detekt") version "1.23.7"
}

group = "com.algamja"
version = "0.0.1"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot 기본
    implementation("org.springframework.boot:spring-boot-starter")           // 로깅 등
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation(libs.spring.boot.starter.actuator)

    // ✅ Exposed를 위한 JDBC 스타터 추가
    implementation(libs.spring.boot.starter.jdbc)

    // OAuth2
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    // Swagger
    implementation(libs.springdoc.webmvc.ui)

    // JWT
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    // Jackson / Kotlin
    implementation(libs.jackson.module.kotlin)
    implementation(libs.kotlin.reflect)

    // Firebase Admin SDK
    implementation(libs.firebase.admin)

    // Exposed
    implementation(libs.exposed.spring.boot.starter)

    // DB 드라이버 (profile로 관리 권장: 로컬 H2 / 운영 MySQL)
    runtimeOnly(libs.h2)
    implementation(libs.mysql.connector.j)

    implementation(libs.jsoup)
    implementation(libs.jasypt.spring.boot.starter)
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.slack.api.client)
    implementation(libs.spring.boot.starter.validation)
    
    // Selenium WebDriver
    implementation("org.seleniumhq.selenium:selenium-java:4.15.0")
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.15.0")
    implementation("org.seleniumhq.selenium:selenium-support:4.15.0")
    
    // OkHttp with Brotli support
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-brotli:4.12.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation(libs.mockito.kotlin)

    // Flyway
    implementation(libs.flyway.core)
    implementation(libs.flyway.mysql)
}

// 테스트 설정
tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
    
    // 테스트 실행 시 메모리 설정
    jvmArgs = listOf("-Xmx2g", "-XX:MaxMetaspaceSize=512m")
    
    // 테스트 결과 보고서 생성
    reports {
        junitXml.required.set(true)
        html.required.set(true)
    }
    
    // 테스트 병렬 실행
    maxParallelForks = Runtime.getRuntime().availableProcessors().div(2).takeIf { it > 0 } ?: 1
    
    // 테스트 타임아웃 설정
    timeout.set(Duration.ofMinutes(10))
}

// Jacoco 테스트 커버리지 설정
jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    
    executionData.setFrom(fileTree(layout.buildDirectory.dir("jacoco")).include("**/*.exec"))
    
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.70".toBigDecimal() // 70% 이상 커버리지 요구
            }
        }
    }
}

// Detekt 정적 분석 설정
detekt {
    config.setFrom("$projectDir/detekt-config.yml")
    buildUponDefaultConfig = true
    autoCorrect = true
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
    }
}

// 빌드 시 테스트가 실행되도록 의존성 설정
tasks.build {
    dependsOn(tasks.test)
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xannotation-default-target=param-property"))
}

configurations.all {
    exclude(group = "io.springfox")
}

// 리소스 필터링 (application.yml의 @project.version@ 등 치환)
tasks.processResources {
    expand(project.properties)
    inputs.property("version", version)
}

tasks.jar { enabled = false }

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("app.jar")
}