plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.gradle.plugin-publish")
    `java-gradle-plugin`
    `maven-publish`
}

group = "de.menkalian.vela"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

pluginBundle {
    website = "https://gitlab.com/kiliankra/vela"
    vcsUrl = "https://gitlab.com/kiliankra/vela.git"
    tags = listOf("tool", "kotlin", "java", "keys", "generation")
}

gradlePlugin {
    plugins {
        create("backgroundPlugin") {
            id = "de.menkalian.vela.keygen"
            displayName = "Key Object Generator Plugin"
            description = "A plugin that generates objects to programatically acces keys."
            implementationClass = "de.menkalian.vela.KeyObjectGradlePlugin"
        }
    }
}

publishing {
    repositories {
        maven {
            url = uri("http://server.menkalian.de:8081/artifactory/vela")
            name = "artifactory-menkalian"
            credentials {
                username = System.getenv("MAVEN_REPO_USER")
                password = System.getenv("MAVEN_REPO_PASS")
            }
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(jackson("core"))
    implementation(jackson("databind"))
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.0")

    compileOnly("com.android.tools.build:gradle:4.1.1")
}

fun jackson(module: String) = "com.fasterxml.jackson.core:jackson-$module:2.12.0"
