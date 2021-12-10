rootProject.name = "vela"

// Plugins
include(
    "plugin-backgrounds",
    "plugin-buildconfig",
    "plugin-keygen",
    "plugin-versioning"
)

// Tools
include(
    "tool-epc",
    "tool-template"
)

// Libraries
include(
    "lib-transfervalue"
)

// featuretoggle
include(
    "lib-featuretoggle",
    "lib-featuretoggle:compiler",
    "lib-featuretoggle:plugin"
)

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            setUrl("https://artifactory.menkalian.de/artifactory/menkalian")
            name = "artifactory-menkalian"
        }
    }
}
