tasks.getByName("bootJar") {
    enabled = true
}

dependencies {
    implementation(project(":core"))

    // quartz
    implementation("org.springframework.boot:spring-boot-starter-quartz")

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2") // 코루틴 라이브러리
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.2") // 코루틴 reactor 라이브러리
}