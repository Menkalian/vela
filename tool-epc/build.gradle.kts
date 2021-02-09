plugins {
    java
    kotlin("jvm")
    `maven-publish`
    jacoco
}

version = "1.0.0"

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.isEnabled = true
        csv.isEnabled = true
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}