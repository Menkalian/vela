plugins {
    java
    kotlin("jvm")
    id("org.jetbrains.dokka")

    `maven-publish`
    jacoco
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

dependencies {}
