plugins {
    java
    id("org.springframework.boot")
}

// Only infrastructure produces a runnable jar
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
    archiveFileName.set("fintrack.jar")
}

// All other modules produce plain jars
tasks.getByName<Jar>("jar") {
    enabled = true
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":adapter-in-rest"))
    implementation(project(":adapter-in-kafka"))
    implementation(project(":adapter-out-persistence"))
    implementation(project(":adapter-out-redis"))
    implementation(project(":adapter-out-kafka"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql:1.20.1")
    testImplementation("org.testcontainers:kafka:1.20.1")
    testImplementation("org.testcontainers:junit-jupiter:1.20.1")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")
}