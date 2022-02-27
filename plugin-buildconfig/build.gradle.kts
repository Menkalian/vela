plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.gradle.plugin-publish")
    `maven-publish`
    jacoco
    id("org.jetbrains.dokka")
}

gradlePlugin {
    plugins {
        create("keygenPlugin") {
            id = "de.menkalian.vela.buildconfig"
            implementationClass = "de.menkalian.vela.gradle.BuildconfigGradlePlugin"
            displayName = "Build Config Generator Plugin"
            description = ""
        }
    }
}

dependencies {}
