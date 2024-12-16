import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.21"
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management") apply false
    kotlin("plugin.spring") version "1.9.21" apply false
    kotlin("plugin.jpa") version "1.9.21" apply false
    kotlin("kapt") version "1.9.21" apply false
}

allprojects {
    val projectGroup: String by project
    val applicationVersion: String by project

    group = projectGroup
    version = applicationVersion

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen") // 추가

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        implementation("org.jetbrains.kotlin:kotlin-reflect")

        // web
        implementation("org.springframework.boot:spring-boot-starter-web")
    }

    tasks.getByName("bootJar") {
        enabled = false
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += listOf("-Xjsr305=strict")
            jvmTarget = "21"
        }
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

}
