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

## Available plugins/tools

### Key-Object-Generator

- **type**: Gradle plugin (ID: `de.menkalian.vela.keygen`)
- **version**: `1.1.0`
- **description**:
  If you Identify some values by a string-key like `Vela.Example.Key`, `Menkalian/Repositories/Vela` or `Staged~Variable~Access`, you may want to generate classes/objects to represent these, to have autocomplete in your IDE and minimalize typos. Vela generates those objects for you, while you only have to supply a file in a simple, yaml-like format.
- **details**: [specific README](key-object-generator-plugin/README.md)