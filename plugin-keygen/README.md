# Key-Object-Generator

Apply this plugin with Groovy-DSL:

```groovy
plugins {
    id 'de.menkalian.vela.keygen' version '1.1.0'
}
```

Kotlin-DSL:

```kotlin
plugins {
    id("de.menkalian.vela.keygen") version "1.1.0"
}
```

## What does it do?

If you Identify some values by a string-key like `Vela.Example.Key`, `Menkalian/Repositories/Vela` or `Staged~Variable~Access`, you may want to generate classes/objects to represent these, to have autocomplete in your IDE and minimize typos. Vela generates those objects for you, while you only have to supply a file in a simple, yaml-like format.

## How to use

This Plugin adds a task called `generateKeyObjects` to your Gradle Project (the compileJava-Tasks are dependent on it). By default, this task searches for files in the directory `{projectDir}/src/main/ckf`. It parses every file to an object and generates JavaClasses for that. The generated sources are located in `{buildDir/generated/vela/keyobject/java`. This directory is added as a source-directory for java (you **need** either the `java`-plugin or any `android`-plugin).

The Files in the sources-directory should have a yaml-derived format (I like to call it `compact key format` = `ckf`, but that's nothing official). YAML-Syntax is supported, but since you should only use single-word-strings you may just leave the colon (`:`). An example for a valid file would be:

```
Vela
  Version
  Keys
    Example
    Devices
      Android
      JVM
        Version_1_8
        Version_10
    Type: Size
Aquila:
  Internal
  System:
    - Name
    - Id
    - Password
```

The requirements for a single key-component are identical with those of a java-classname.

## Configurability

The plugin may be configured directly via your `build.gradle[.kts]` (Syntax should be nearly identical). The default configuration would look something like this (with Kotlin DSL):

```kotlin
keygen {
    targetDir = File(buildDir, "generated/vela/keyobject/java").toURI()
    sourceDir = File(projectDir, "src/main/ckf").toURI()

    separator = "."
    targetPackage = "de.menkalian.vela.generated"

    scanRecursive = false
    prefixRecursive = false
    finalLayerAsString = false
}
```

This table contains an explanation for all configurable parameters:

|property            | type              | description | 
|--------------------|-------------------|-------------|
| targetDir          | `URI` (Directory) | The directory generated Files are written in |
| sourceDir          | `URI` (Directory) | The directory containing source-files |
| separator          | `String`          | The separator to be placed between the components (e.g. `.`, `/`) |
| targetPackage      | `String`          | The java-package where generated files are located |
| scanRecursive      | `Boolean`         | Whether subdirectories of `sourceDir` should be scanned |
| prefixRecursive    | `Boolean`         | If source-files are placed in a subdirectory and this is true, then the generated values will be prefixed according to their location relative to `sourceDir` |
| finalLayerAsString | `Boolean`         | Whether the lowest Elements should be Strings or objects (so `GeneratedValue#toString` needs to be called) |

### Generating more than one set

If you want to generate more than one set of keys (that might be for various reasons, like different separators or reusability of repositories), you may specify a second set of generation targets, by creating a new container in `furtherConfigs`.
The way you do this (in a Kotlin-Script) looks like this:
```kotlin
keygen {
    /*... configs for primary set*/
    furtherConfigs {
        create("secondaryKeys") {
            /*... same configs as above are possible here*/
        }
    }
}
```