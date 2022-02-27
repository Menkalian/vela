import java.net.URL

plugins {
    java
    kotlin("jvm")
    `maven-publish`
    jacoco

    id("org.jetbrains.dokka")
    id("de.menkalian.vela.keygen") version "1.2.1"
}

tasks.test {
    testLogging.showStandardStreams = true
}

dependencies {
    implementation(project(":tool-template"))
}

keygen {
    finalLayerAsString = true
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
