@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.gradle.plugin-publish")
    `maven-publish`
    jacoco
}

version = "1.2.0"

gradlePlugin {
    plugins {
        create("keygenPlugin") {
            id = "de.menkalian.vela.keygen"
            implementationClass = "de.menkalian.vela.gradle.KeyObjectGradlePlugin"
            displayName = "Key Object Generator Plugin"
            description = ""
        }
    }
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-core:2.12.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.0")

    compileOnly("com.android.tools.build:gradle:4.1.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

tasks {
    test {
        useJUnitPlatform {
            includeTags("unit")
        }
    }
}

val testSourceSet = sourceSets.getByName("test")
val integrationTestTask = tasks.register<Test>("integrationTest") {
    description = "Runs the integration tests."
    group = "verification"
    testClassesDirs = testSourceSet.output.classesDirs
    classpath = testSourceSet.runtimeClasspath
    mustRunAfter(tasks.test)
    useJUnitPlatform {
        includeTags("integration")
    }
}
val functionalTestTask = tasks.register<Test>("functionalTest") {
    description = "Runs the functional tests."
    group = "verification"
    testClassesDirs = testSourceSet.output.classesDirs
    classpath = testSourceSet.runtimeClasspath
    mustRunAfter(tasks.test)
    useJUnitPlatform {
        includeTags("functional")
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test, integrationTestTask, functionalTestTask)

    executionData(integrationTestTask.get())
    executionData(functionalTestTask.get())

    reports {
        xml.isEnabled = true
        csv.isEnabled = true
    }
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

tasks.check {
    dependsOn(integrationTestTask)
    dependsOn(functionalTestTask)
    dependsOn(tasks.jacocoTestReport)
}