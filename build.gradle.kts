@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("jvm") version "1.4.30" apply false
    id("com.gradle.plugin-publish") version "0.12.0" apply false
}

group = "de.menkalian.vela"

allprojects {
    group = "de.menkalian.vela"

    repositories {
        mavenCentral()
        jcenter()
        google()
    }

    pluginManager.withPlugin("com.gradle.plugin-publish") {
        extensions.getByType(com.gradle.publish.PluginBundleExtension::class.java).apply {
            website = "https://gitlab.com/kiliankra/vela"
            vcsUrl = "https://gitlab.com/kiliankra/vela.git"
            tags = listOf("tool", "vela")
        }
    }

    pluginManager.withPlugin("maven-publish") {
        extensions.getByType(PublishingExtension::class.java).apply {
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
    }

    pluginManager.withPlugin("java") {
        extensions.getByType(JavaPluginExtension::class.java).apply {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
            withJavadocJar()
            withSourcesJar()
        }

        dependencies {
            add("implementation", kotlin("stdlib"))
            add("implementation", kotlin("stdlib-jdk8"))
            add("implementation", kotlin("stdlib-jdk7"))
            add("implementation", kotlin("reflect"))
        }
    }
}
