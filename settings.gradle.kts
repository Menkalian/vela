rootProject.name = "vela"

// Plugins
include(
    "plugin-buildconfig",
    "plugin-featuretoggle",
    "plugin-featuretoggle:compiler",
    "plugin-featuretoggle:plugin",
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

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            setUrl("https://artifactory.menkalian.de/artifactory/menkalian")
            name = "artifactory-menkalian"
        }
    }
}
