plugins {
    java
    kotlin("jvm")
    kotlin("plugin.serialization")
    `maven-publish`
    jacoco
    id("org.jetbrains.dokka")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

dependencies {}
