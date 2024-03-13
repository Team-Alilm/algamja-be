import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.2" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22" apply false
    kotlin("plugin.jpa") version "2.0.0-Beta4" apply false
}

allprojects {
    group = "org.teamAlilm"
    version = "0.0.1-SNAPSHOT"

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    repositories {
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    dependencies {
        // default
        implementation("org.springframework.boot:spring-boot-starter-web")
        annotationProcessor("org.projectlombok:lombok")

        // jpa
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")

        //oauth2
        implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

        // security
        implementation("org.springframework.boot:spring-boot-starter-security")
        testImplementation("org.springframework.security:spring-security-test")

        // jwt
        implementation("io.jsonwebtoken:jjwt-api:0.12.3")
        runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
        runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")


        // database
        runtimeOnly("com.h2database:h2")
        runtimeOnly("com.mysql:mysql-connector-j")

        // validation
        implementation("org.springframework.boot:spring-boot-starter-validation")

        // swagger
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")

        // jsoup
        implementation("org.jsoup:jsoup:1.17.2")

        implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5")
    }
}

