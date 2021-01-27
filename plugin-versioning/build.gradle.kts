plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.gradle.plugin-publish")
    `maven-publish`
}

version = "1.0.0"

gradlePlugin {
    plugins {
        create("versioningPlugin") {
            id = "de.menkalian.vela.versioning"
            implementationClass = "de.menkalian.vela.VersioningGradlePlugin"
            displayName = "Versioning Plugin"
            description = ""
        }
    }
}

dependencies {
    compileOnly("com.android.tools.build:gradle:4.1.1")
}
