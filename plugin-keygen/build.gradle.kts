plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.gradle.plugin-publish")
    `maven-publish`
}

version = "1.1.0"

gradlePlugin {
    plugins {
        create("keygenPlugin") {
            id = "de.menkalian.vela.keygen"
            implementationClass = "de.menkalian.vela.KeyObjectGradlePlugin"
            displayName = "Key Object Generator Plugin"
            description = ""
        }
    }
}

dependencies {
    implementation(jackson("core"))
    implementation(jackson("databind"))
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.0")

    compileOnly("com.android.tools.build:gradle:4.1.1")
}

fun jackson(module: String) = "com.fasterxml.jackson.core:jackson-$module:2.12.0"
