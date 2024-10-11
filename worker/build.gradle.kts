tasks.getByName("bootJar") {
    enabled = true
}

dependencies {
    implementation(project(":core"))

    // quartz
    implementation("org.springframework.boot:spring-boot-starter-quartz")

    // firebase
    implementation("com.google.firebase:firebase-admin:8.1.0")
}