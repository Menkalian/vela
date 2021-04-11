# Key-Object-Generator

Apply this plugin with Groovy-DSL:

```groovy
plugins {
    id 'de.menkalian.vela.versioning' version '1.1.0'
}
```

Kotlin-DSL:

```kotlin
plugins {
    id("de.menkalian.vela.versioning") version "1.1.0"
}
```

## What does it do?

This plugin helps to have your project versioned per build. To acieve this a build-number is exposed via the `versioning.buildNo` variable. Do **NOT** set this variable yourself. If you do anyways the plugin might not work as intended.

## How to use

After adding this plugin to your project you have to decide on to things: <br>
(1) How to store the current build-number <br>
(2) When to increase the build-number

### How to store the build-number

Currently two ways of storing a build-number are supported:

* inside a MariaDB-Database
* inside a `versioning.properties`-file for the project

#### Storing your build-number in a database

To store your build-number in a database you have to provide 4 environmental variables:

|   Variable   |           Meaning           |  Example  | Default
|--------------|-----------------------------|-----------|---------
| VELA_DB_HOST | Host of the MariaDB Server  | localhost |
| VELA_DB_PORT | Port of the MariaDB Server  |   3306    | `3306`
| VELA_DB_NAME | Name of the database to use | versions  | `VELA_VERSIONING`
| VELA_DB_USER | Username for login          | root      | `root`
| VELA_DB_PASS | Password for login          | changeIt  |

This way is chosen if `VELA_DB_HOST` is set.

#### Properties File

To use this way you have to create a file called `versioning.properties` in the base-directory of your project. If that file does not exist AND `VELA_DB_HOST` is not set the build-number will always be `1`.

### When to increase build-number

Firstly you should set the `versioning.upgradeTask` property. If you have situations where you want to prevent the build-number from advancing (e.g. in a CI-pipeline) you may define `VELA_NO_UPGRADE` as an environmental variable. Then the build-number will never be increased.