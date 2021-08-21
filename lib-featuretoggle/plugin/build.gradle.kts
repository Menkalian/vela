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

version = "1.0.1"

gradlePlugin {
    plugins {
        create("featuretogglePlugin") {
            id = "de.menkalian.vela.featuretoggle"
            implementationClass = "de.menkalian.vela.gradle.FeaturetoggleGradlePlugin"
            displayName = "Featuretoggle Generator Plugin"
            description = ""
        }
    }
}

dependencies {
    implementation(project(":lib-featuretoggle:compiler"))

    compileOnly("com.android.tools.build:gradle:4.1.1")
}
