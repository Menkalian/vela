plugins {
    java
    kotlin("jvm")

    `maven-publish`
    jacoco

    id("de.menkalian.vela.buildconfig")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

dependencies {
}

sourceSets.create("example") {
    compileClasspath += sourceSets.getByName("main").output
    runtimeClasspath += sourceSets.getByName("main").output
}
