dependencies {
    implementation(project(":domain"))

    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // jsoup
    implementation("org.jsoup:jsoup:1.15.3")

    // mail
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // slack
    implementation("com.slack.api:slack-api-client:1.42.0")

    // jasypt
    implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5")

    // h2
    runtimeOnly("com.h2database:h2")

    //mysql
    runtimeOnly("com.mysql:mysql-connector-j")

    // firebase
    implementation("com.google.firebase:firebase-admin:8.1.0")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}
