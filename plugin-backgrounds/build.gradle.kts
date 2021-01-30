plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.gradle.plugin-publish")
    `maven-publish`
}

version = "1.0.0"

gradlePlugin {
    plugins {
        create("backgroundPlugin") {
            id = "de.menkalian.vela.backgrounds"
            implementationClass = "de.menkalian.vela.BackgroundGradlePlugin"
            displayName = "Background Generator Plugin"
            description = ""
        }
    }
}

dependencies {
    compileOnly("com.android.tools.build:gradle:4.1.1")
}
