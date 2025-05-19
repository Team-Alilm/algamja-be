tasks.getByName("bootJar") {
    enabled = true
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":security"))

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    // security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.security:spring-security-test")
}
