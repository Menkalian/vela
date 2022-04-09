@file:Suppress("SpellCheckingInspection")

import org.jetbrains.dokka.gradle.DokkaTaskPartial

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

tasks.withType(DokkaTaskPartial::class.java).configureEach {
    moduleName.set("plugin-featuretoggle")
}

dependencies {
    implementation(project(":plugin-featuretoggle:compiler"))

    compileOnly("com.android.tools.build:gradle:4.1.1")
}
