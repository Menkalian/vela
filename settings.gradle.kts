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
    "lib-featuretoggle:compiler"
)

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("http://server.menkalian.de:8081/artifactory/vela")
            name = "artifactory-menkalian"
            isAllowInsecureProtocol = true
        }
    }
}
