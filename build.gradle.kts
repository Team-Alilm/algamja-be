import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    java
}

group = "com.skylabs"
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

    // ✅ JPA 제거
    // implementation("org.springframework.boot:spring-boot-starter-data-jpa")

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

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    // Flyway
    implementation(libs.flyway.core)
    implementation(libs.flyway.mysql)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xannotation-default-target=param-property"))
}

configurations.all {
    exclude(group = "io.springfox")
}

tasks.jar { enabled = false }

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("app.jar")
}