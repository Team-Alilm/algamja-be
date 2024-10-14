tasks.getByName("bootJar") {
    enabled = true
}

dependencies {
    implementation(project(":core"))

    // quartz
    implementation("org.springframework.boot:spring-boot-starter-quartz")

    // web
    implementation("org.springframework.boot:spring-boot-starter-web")
}