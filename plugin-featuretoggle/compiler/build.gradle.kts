plugins {
    java
    kotlin("jvm")
    `maven-publish`
    jacoco

    id("org.jetbrains.dokka")
    id("de.menkalian.vela.keygen")
}

tasks.test {
    testLogging.showStandardStreams = true
}

dependencies {
    implementation(project(":tool-template"))
}

keygen {
    finalLayerAsString = true
}

tasks.withType(org.jetbrains.dokka.gradle.DokkaTaskPartial::class.java).configureEach {
    moduleName.set("lib-featuretoggle-compiler")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
