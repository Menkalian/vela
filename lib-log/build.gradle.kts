plugins {
    java
    `maven-publish`
    jacoco

    kotlin("jvm")
    kotlin("plugin.serialization")

    id("org.jetbrains.dokka")
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            dependencies {
                // SLF4J NOOP
                implementation("org.slf4j:slf4j-nop:2.0.0")
            }
        }
        create<MavenPublication>("slf4jcompatible") {
            this.artifactId += "-slf4j-s2s"
            from(components["java"])
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation("org.jetbrains.exposed:exposed-core:0.39.2")
    implementation("org.jetbrains.exposed:exposed-dao:0.39.2")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.39.2")

    // Database Drivers
    runtimeOnly("org.xerial:sqlite-jdbc:3.39.2.0")

    // SLF4J NOOP
    implementation("org.slf4j:slf4j-nop:2.0.0")
}
