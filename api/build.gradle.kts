tasks.getByName("bootJar") {
    enabled = true
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))

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
}
