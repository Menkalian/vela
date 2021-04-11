plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    jacoco

    id("com.gradle.plugin-publish")
    `maven-publish`
}

version = "1.1.0"

gradlePlugin {
    plugins {
        create("versioningPlugin") {
            id = "de.menkalian.vela.versioning"
            implementationClass = "de.menkalian.vela.gradle.VersioningGradlePlugin"
            displayName = "Versioning Plugin"
            description = ""
        }
    }
}

dependencies {
    compileOnly("com.android.tools.build:gradle:4.1.1")

    runtimeOnly("org.mariadb.jdbc:mariadb-java-client:2.7.2")

    testImplementation("ch.vorburger.mariaDB4j:mariaDB4j:2.4.0")
    testImplementation("com.github.stefanbirkner:system-lambda:1.2.0")
    testImplementation(project(":tool-epc"))
}
