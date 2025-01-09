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

    // selenium
    implementation("org.seleniumhq.selenium:selenium-java:4.27.0")
    implementation("io.github.bonigarcia:webdrivermanager:5.9.2")
    implementation("org.seleniumhq.selenium:selenium-api:4.27.0")
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.27.0")
}