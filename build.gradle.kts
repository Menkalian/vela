@file:Suppress("UnstableApiUsage")

import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.6.10"
    val gradlePublishVersion = "0.20.0"

    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
    id("org.jetbrains.dokka") version kotlinVersion
    id("com.gradle.plugin-publish") version gradlePublishVersion apply false

    val usingVelaVersion = "2.0.0"
    id("de.menkalian.vela.buildconfig") version "1.0.1" apply false
    id("de.menkalian.vela.featuretoggle") version "1.0.1" apply false
    id("de.menkalian.vela.keygen") version "1.2.1" apply false
    id("de.menkalian.vela.versioning") version "1.1.0" apply false
}


val velaBaseVersion = "2.0.0"
val velaVersion = parseVelaVersion(velaBaseVersion)
val junitVersion = "5.8.2"

fun parseVelaVersion(baseVersion: String = velaBaseVersion): String {
    // Expecting tag like `release-2.3.1-alpha4`
    val buildTag = System
        .getenv("CI_COMMIT_TAG")
        ?.substring("release-".length)
    val commitSha = System.getenv("CI_COMMIT_SHA") ?: "dev"

    return buildTag ?: "$baseVersion-$commitSha"
}

allprojects {
    group = "de.menkalian.vela"
    version = velaVersion

    repositories {
        mavenCentral()
        google()
    }

    pluginManager.withPlugin("java") {
        extensions.getByType(JavaPluginExtension::class.java).apply {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
            withJavadocJar()
            withSourcesJar()
        }

        dependencies {
            add("testImplementation", "org.junit.jupiter:junit-jupiter-api:${junitVersion}")
            add("testImplementation", "org.junit.jupiter:junit-jupiter-params:${junitVersion}")
            add("testRuntimeOnly", "org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
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
                this.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
            }
        }
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.plugin.serialization") {
        dependencies {
            add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.2")
        }
    }

    pluginManager.withPlugin("org.jetbrains.dokka") {
        val kotlinVersion = "1.6.10"
        dependencies.add("dokkaHtmlPlugin", "org.jetbrains.dokka:kotlin-as-java-plugin:$kotlinVersion")
        tasks.withType(DokkaTask::class.java).configureEach {
            try {
                dokkaSourceSets.named("main") {
                    platform.set(org.jetbrains.dokka.Platform.jvm)
                    sourceLink {
                        localDirectory.set(project.file("src/main/kotlin"))
                        remoteUrl.set(uri("https://gitlab.com/kiliankra/vela/-/tree/main/${projectDir.relativeTo(rootProject.projectDir)}/src/main/kotlin").toURL())
                        remoteLineSuffix.set("#L")
                    }
                }
            } catch (_: Exception) {}
        }
    }

    pluginManager.withPlugin("jacoco") {
        tasks.withType(JacocoReport::class.java) {
            dependsOn(tasks.getByName("test"))

            classDirectories.setFrom(files(classDirectories.files.map {
                fileTree(
                    mapOf(
                        "dir" to it,
                        "exclude" to listOf("de/menkalian/vela/gradle", "de/menkalian/vela/example")
                    )
                )
            }))

            reports {
                xml.required.set(true)
                csv.required.set(true)
            }
        }

        tasks.getByName("check") {
            dependsOn(tasks.withType(JacocoReport::class.java))
        }
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
                    setUrl("https://artifactory.menkalian.de/artifactory/vela")
                    name = "artifactory-menkalian"
                    credentials {
                        username = System.getenv("MAVEN_REPO_USER")
                        password = System.getenv("MAVEN_REPO_PASS")
                    }
                }
            }
        }
    }
}

tasks.dokkaHtmlMultiModule.configure {
    moduleName.set("Vela Source Documentation")
}
