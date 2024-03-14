import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
    kotlin("plugin.spring") apply false
    kotlin("plugin.jpa") apply false
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

val projectGroup: String by project
val applicationVersion: String by project

allprojects {
    group = projectGroup
    version = applicationVersion

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    dependencyManagement {
        val springCloudVersion: String by project

        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
        }
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        implementation("org.jetbrains.kotlin:kotlin-reflect")

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

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
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