plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.gradle.plugin-publish")
    `maven-publish`
    jacoco
}

version = "1.0.0"

gradlePlugin {
    plugins {
        create("keygenPlugin") {
            id = "de.menkalian.vela.buildconfig"
            implementationClass = "de.menkalian.vela.gradle.BuildconfigGradlePlugin"
            displayName = "Build Config Generator Plugin"
            description = ""
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

tasks {
    test {
        useJUnitPlatform()
    }
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        csv.isEnabled = true
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}
