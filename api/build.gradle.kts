tasks.getByName("bootJar") {
    enabled = true
}

dependencies {
    implementation(project(":core"))

    // oauth2
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    // validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // security
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")
}