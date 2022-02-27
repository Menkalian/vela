# *vela* - development tools, libraries and plugins

This repository contains various tools, libraries or plugins. I created them for my personal use. but if you find any of them useful you are free to use them for yourself (without ANY WARRANTIES).

The name *vela* means **sail** and I chose it, because these tool should accelerate your development or project :slight_smile:.

## Using *vela*

To use components from *vela* in your projects you can utilize the prebuilt versions from my [artifactory](https://artifactory.menkalian.de).

To include any libraries use the following declaration in your `build.gradle[.kts]`:

````kotlin
repositories {
    maven {
        name = "artifactory-menkalian"
        url = uri("https://artifactory.menkalian.de/artifactory/menkalian")
    }
}
````

To use the gradle plugins include the following in your `settings.gradle[.kts]`:

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            name = "artifactory-menkalian"
            url = uri("https://artifactory.menkalian.de/artifactory/menkalian")
        }
    }
}
```

---

## Versions

Starting with version `2.0.0` all plugins share the same versioning. Increments in the version are also marked by git tags (`release-2.x.x`). It is recommended to use the latest version for all components.

---

## Available components

| Name                                                   | Typ             | id/artifact                                    | Platform                      | Beschreibung                                                                                                         |
|--------------------------------------------------------|-----------------|------------------------------------------------|-------------------------------|----------------------------------------------------------------------------------------------------------------------|
| [**BuildConfig**](plugin-buildconfig/README.md)        | *gradle plugin* | `de.menkalian.vela.buildconfig`                | `java` `kotlin-jvm`           | Generates a class `BuildConfig` which contains group, name, version and other properties of the project.             |
| [**Featuretoggle**](plugin-featuretoggle/README.md)    | *gradle plugin* | `de.menkalian.vela.featuretoggle`              | `java` `android` `kotlin-jvm` | Generates Config-Klasses based on a xml-document.                                                                    |
| [**KeyGenerator**](plugin-keygen/README.md)            | *gradle plugin* | `de.menkalian.vela.keygen`                     | `java` `android` `kotlin-jvm` | Generates String constants for variables like `Vela.Example.Key` or `Menkalian/Repositories/Vela`.                   |
| [**Versioning**](plugin-versioning/README.md)          | *gradle plugin* | `de.menkalian.vela.versioning`                 | `java` `android` `kotlin-jvm` | Exposes a variable called `versioning.buildNo` which contains the amount of times this project was built.            |
|                                                        |                 |                                                |                               |                                                                                                                      |
| [**Transferable values**](lib-transfervalue/README.md) | *library*       | `de.menkalian.vela:lib-transfervalue:$version` | `java` `android` `kotlin-jvm` | Allows variables of different (basic) types to be transferred as the same datatype.                                  |
|                                                        |                 |                                                |                               |                                                                                                                      |
| [**Editable Project Compactor**](tool-epc/README.md)   | *tool*          | `de.menkalian.vela:tool-epc:$version`          | `java` `android` `kotlin-jvm` | Library/Tool to bundle/extract directories to/from a simple text format (to enable easy editing and creating diffs). |
| [**Template**](tool-template/README.md)                | *tool*          | `de.menkalian.vela:tool-template:$version`     | `java` `android` `kotlin-jvm` | (turing-complete) template engine :slight_smile:                                                                     |
