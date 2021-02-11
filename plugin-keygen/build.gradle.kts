@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.gradle.plugin-publish")
    `maven-publish`
    jacoco
}

tasks.test {
    testLogging.showStandardStreams = true
}

version = "1.2.1"

gradlePlugin {
    plugins {
        create("keygenPlugin") {
            id = "de.menkalian.vela.keygen"
            implementationClass = "de.menkalian.vela.gradle.KeyObjectGradlePlugin"
            displayName = "Key Object Generator Plugin"
            description = ""
        }
    }
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-core:2.12.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.0")

    compileOnly("com.android.tools.build:gradle:4.1.1")

    testImplementation(project(":tool-epc"))
}
