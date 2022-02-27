plugins {
    java
    kotlin("jvm")
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
