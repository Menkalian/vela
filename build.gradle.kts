@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31" apply false
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
                    isAllowInsecureProtocol = true
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
            add("testImplementation", "org.junit.jupiter:junit-jupiter-api:5.7.0")
            add("testImplementation", "org.junit.jupiter:junit-jupiter-params:5.7.0")
            add("testRuntimeOnly", "org.junit.jupiter:junit-jupiter-engine:5.7.0")
        }

        tasks {
            withType(Test::class.java) {
                useJUnitPlatform()
                testLogging {
                    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                }
            }
        }
    }

    pluginManager.withPlugin("jacoco") {
        tasks.withType(JacocoReport::class.java) {
            dependsOn(tasks.getByName("test"))

            classDirectories.setFrom(files(classDirectories.files.map {
                fileTree(
                    mapOf(
                        "dir" to it,
                        "exclude" to "de/menkalian/vela/gradle"
                    )
                )
            }))

            reports {
                xml.isEnabled = true
                csv.isEnabled = true
            }
        }

        tasks.getByName("check") {
            dependsOn(tasks.withType(JacocoReport::class.java))
        }
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        dependencies {
            add("implementation", kotlin("stdlib"))
            add("implementation", kotlin("stdlib-jdk8"))
            add("implementation", kotlin("stdlib-jdk7"))
            add("implementation", kotlin("reflect"))
        }

        tasks.withType(KotlinCompile::class.java) {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_1_8.toString()
            }
        }
    }
}