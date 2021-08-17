plugins {
    java
    kotlin("jvm")
    `maven-publish`
    jacoco
}

version = "1.0.0"

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

dependencies {
}

sourceSets.create("example") {
    compileClasspath += sourceSets.getByName("main").output
    runtimeClasspath += sourceSets.getByName("main").output
}
