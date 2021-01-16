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
    tags = listOf("tool", "kotlin", "java", "android", "versioning")
}

gradlePlugin {
    plugins {
        create("versioningPlugin") {
            id = "de.menkalian.vela"
            displayName = "Versioning Plugin"
            description = "A plugin that enables Versioning per build for Android projects."
            implementationClass = "de.menkalian.vela.VersioningGradlePlugin"
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
    compileOnly("com.android.tools.build:gradle:4.1.1")
}
