plugins {
    java
    kotlin("jvm")
    kotlin("plugin.spring") version "2.2.0" // ✅ 자동 open 처리
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.skylabs"
version = "0.0.1-SNAPSHOT"

kotlin {
    jvmToolchain(21) // Java 21 명확하게 지정
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")

    // web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    // security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.security:spring-security-test")

    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // jwt
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")

    // oauth2
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}