import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
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
    // Spring Boot 스타터 (버전 생략 = Boot BOM 관리)
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    // Swagger
    implementation(libs.springdoc.webmvc.ui)

    // JWT
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    // Jackson / Kotlin
    implementation(libs.jackson.module.kotlin)
    implementation(libs.kotlin.reflect)

    // Firebase Admin SDK (FCM)
    implementation(libs.firebase.admin)

    // Exposed
    implementation(libs.exposed.spring.boot.starter)

    // H2 (local/test)
    runtimeOnly(libs.h2)

    implementation(libs.jsoup)

    implementation(libs.jasypt.spring.boot.starter)

    implementation(libs.spring.boot.starter.mail)

    implementation(libs.slack.api.client)

    implementation(libs.spring.boot.starter.validation)
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

tasks.jar {
    enabled = false
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("app.jar")
}