@file:Suppress("SpellCheckingInspection")

plugins {
    `java-gradle-plugin`
    `maven-publish`
    jacoco

    kotlin("jvm")

    id("com.gradle.plugin-publish")
    id("org.jetbrains.dokka")
}

tasks.test {
    testLogging.showStandardStreams = true
}

gradlePlugin {
    plugins {
        create("keygenPlugin") {
            id = "de.menkalian.vela.keygen"
            implementationClass = "de.menkalian.vela.gradle.KeygenGradlePlugin"
            displayName = "Key Object Generator Plugin"
            description = ""
        }
    }
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-core:2.12.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.0")

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    implementation("com.android.tools.build:gradle:7.1.1")

    implementation(project(":tool-template"))
    testImplementation(project(":tool-epc"))
}
