plugins {
    java
    kotlin("jvm")
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
