dependencies {
    implementation(project(":core"))

    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // security
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")

    // jwt
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")

    // oauth2
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
}