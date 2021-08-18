import java.net.URL

plugins {
    java
    kotlin("jvm")
    `maven-publish`
    jacoco

    id("org.jetbrains.dokka")
    id("de.menkalian.vela.keygen") version "1.2.1"
}

tasks.test {
    testLogging.showStandardStreams = true
}


version = "1.0.0"

dependencies {
    implementation(project(":tool-template"))
}

keygen {
    finalLayerAsString = true
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    dokkaSourceSets {
        named("main"){
            moduleName.set("Vela Compiler for Featuretoggles")
            this.platform.set(org.jetbrains.dokka.Platform.jvm)
            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(URL("https://gitlab.com/kiliankra/vela/-/tree/main/lib-transfervalue/src/main/kotlin"))
                remoteLineSuffix.set("#L")
            }
        }
    }
}
