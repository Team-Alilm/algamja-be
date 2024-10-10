tasks.getByName("bootJar") {
    enabled = true
}

dependencies {
    implementation(project(":core"))
}