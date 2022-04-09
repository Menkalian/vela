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
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

dependencies {}
