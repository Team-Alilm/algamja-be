tasks.getByName("bootJar") {
    enabled = true
}

dependencies {
    implementation(project(":domain"))

    // jasypt
    implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5")

    // sqs
    implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:3.0.4"))
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")

    // jsoup
    implementation("org.jsoup:jsoup:1.15.3")

    // slack
    implementation("com.slack.api:slack-api-client:1.42.0")
}