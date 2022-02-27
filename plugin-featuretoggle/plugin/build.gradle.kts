@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.gradle.plugin-publish")
    `maven-publish`
    jacoco
    id("org.jetbrains.dokka")
}

tasks.test {
    testLogging.showStandardStreams = true
}

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
    implementation(project(":plugin-featuretoggle:compiler"))

    compileOnly("com.android.tools.build:gradle:4.1.1")
}
