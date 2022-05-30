# plugin-buildconfig

This is a gradle plugin to expose information about your build to your compiled program in a quick and easy way.
After applying this plugin to your gradle project you can configure it with the `buildconfig`-extension (the shown values are the default):

````kotlin
buildconfig {
    targetDir.set(project.file("build/generated/vela/buildconfig")) // Adapt the directory where the sources are generated
    targetPackage.set(project.group) // Set the target package of the generated sources
    generator.set(Generator.DEFAULT) // Generator to use. May be one of [JAVA, KOTLIN, DEFAULT]

    // Configure the values which are contained in the buildconfig as constants.
    additionalProperties.clear()
    additionalProperties["group"] = { it.group.toString() }
    additionalProperties["module"] = { it.name.toString() }
    additionalProperties["version"] = { it.version.toString() }
}
````