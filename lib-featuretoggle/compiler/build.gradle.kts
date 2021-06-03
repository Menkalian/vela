import java.net.URL

plugins {
    java
    kotlin("jvm")
    `maven-publish`
    jacoco
    id("org.jetbrains.dokka")
}

tasks.test {
    testLogging.showStandardStreams = true
}


version = "1.0.0"

dependencies {
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
