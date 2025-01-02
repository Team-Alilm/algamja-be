tasks.getByName("bootJar") {
    enabled = true
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))

    // quartz
    implementation("org.springframework.boot:spring-boot-starter-quartz")

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2") // 코루틴 라이브러리
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.2") // 코루틴 reactor 라이브러리

    //aws
    implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:3.0.4"))
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")
}