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
        create("buildconfigPlugin") {
            id = "de.menkalian.vela.buildconfig"
            implementationClass = "de.menkalian.vela.gradle.BuildconfigGradlePlugin"
            displayName = "Build Config Generator Plugin"
            description = ""
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    implementation("com.android.tools.build:gradle:7.1.1")

    implementation(project(":tool-template"))
    testImplementation(project(":tool-epc"))
}
