tasks.getByName("bootJar") {
    enabled = true
}

dependencies {
    implementation(project(":core"))
    implementation(project(":security"))

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    // validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // security
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")

    //
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

}
