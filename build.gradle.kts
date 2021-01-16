plugins {
    kotlin("jvm") version "1.4.21" apply false
    kotlin("kapt") version "1.4.21" apply false
    id("com.gradle.plugin-publish") version "0.12.0" apply false
}

group = "de.menkalian.vela"
version = "1.0.0"

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        jcenter()
        google()
    }
}
