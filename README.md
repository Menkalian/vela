# Vela DEV-tools

This repository contains various tools (mainly **Gradle-Plugins**) I created for my personal use. If you find any of them useful you are free to use them for yourself (without any warranties from me).

## Using the plugins

If you just want to use plugins from the *Vela*-project you have to add the following to your `settings.gradle[.kts]`:

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("http://server.menkalian.de:8081/artifactory/vela")
            name = "artifactory-menkalian"
        }
    }
}
```

If you want to combine these with any other of my tools (e.g. Auriga) use the combined repository:

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("http://server.menkalian.de:8081/artifactory/menkalian")
            name = "artifactory-menkalian"
        }
    }
}
```

---

## Using libraries/tools

If you want to use libraries of tools from vela you have to add the repository to your build.gradle[.kts]:

```kotlin
  repositories {
      // ... other repos ...
      maven {
          url = uri("http://server.menkalian.de:8081/artifactory/vela")
          name = "artifactory-menkalian"
      }
  }
```

If you want to combine these with any other of my tools (e.g. Auriga) use the combined repository:

```kotlin
    repositories {
      // ... other repos ...
        maven {
            url = uri("http://server.menkalian.de:8081/artifactory/menkalian")
            name = "artifactory-menkalian"
        }
    }
```

---

## Available plugins/tools

### Android-Background-Generator

- **type**: Gradle plugin (ID: `de.menkalian.vela.backgrounds`)
- **version**: `1.0.0`
- **description**:
  Generates animated backgrounds based on your parameters.
- **platforms**: `android`
- **details**: [specific README](plugin-backgrounds/README.md)

### BuildConfig-Generator

- **type**: Gradle plugin (ID: `de.menkalian.vela.buildconfig`)
- **version**: `1.0.0`
- **description**:
  Generates a class called BuildConfig (in the package corresponding to the project-group), that holds group, name and version of the project.
- **platforms**: `java` `kotlin-jvm`<sup>1</sup>
- **details**: [specific README](plugin-buildconfig/README.md)

### Key-Object-Generator

- **type**: Gradle plugin (ID: `de.menkalian.vela.keygen`)
- **version**: `1.2.1`
- **description**:
  If you Identify some values by a string-key like `Vela.Example.Key`, `Menkalian/Repositories/Vela` or `Staged~Variable~Access`, you may want to generate classes/objects to represent these, to have autocomplete in your IDE and minimize typos. Vela generates those objects for you, while you only have to supply a file in a simple, yaml-like format.
- **platforms**: `java` `kotlin-jvm`<sup>1</sup> `android`
- **details**: [specific README](plugin-keygen/README.md)

### Versioning Plugin

- **type**: Gradle plugin (ID: `de.menkalian.vela.versioning`)
- **version**: `1.1.0`
- **description**:
  Exposes a variable called `versioning.buildNo` that contains the amount of times this project was built.
  You may customize the task used to identify a build by setting `versioning.upgradeTask`.
  If this is not set, the `build`-Task is used.
  If that does not exist, every gradle invocation increases the buildnumber.
- **platforms**: `android`, `java`, `kotlin-jvm`
- **details**: [specific README](plugin-versioning/README.md)

### Editable-Project-Compactor

- **type**: Tooling-Lib (Artifact: `de.menkalian.vela:tool-epc:$version`)
- **version**: `1.1.0`
- **description**:
  Allows to pack a full directory/project to a single, editable text-file. There is no compression and all Files are readable/editable in the resulting packed File. The main-purpose of this is to be able to test gradle-plugins with test-projects efficiently. For most other purposes is a compressed-archive-format like `zip, rar, etc.` probably more suitable
- **platforms**: `java` `kotlin-jvm`<sup>1</sup> `android`
- **details**: [specific README](tool-epc/README.md)

###### Footnotes

<span style="color:gray">
1: Needs the <code>java</code>-plugin even for a pure kotlin project, since Java is generated
</span>