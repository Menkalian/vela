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
    website = "https://gitlab.com/kiliankra/auriga"
    vcsUrl = "https://gitlab.com/kiliankra/auriga.git"
    tags = listOf("auriga", "logging", "debug", "tool", "kotlin", "java")
}

gradlePlugin {
    plugins {
        create("aurigaPlugin") {
            id = "de.menkalian.auriga"
            displayName = "Auriga Plugin"
            description = "A plugin that applies the Auriga tools to your projects."
            implementationClass = "de.menkalian.auriga.AurigaGradlePlugin"
        }
    }
}

publishing {
    repositories {
        maven {
            url = uri("http://server.menkalian.de:8081/artifactory/auriga")
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
    compileOnly("com.android.tools.build:gradle:4.1.1")
}
