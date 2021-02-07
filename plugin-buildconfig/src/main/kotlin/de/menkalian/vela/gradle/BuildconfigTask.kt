package de.menkalian.vela.gradle

import de.menkalian.vela.plain.SourceGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class BuildconfigTask : DefaultTask() {
    @Input
    val version = project.version.toString()

    @Input
    val projectGroup = project.group.toString()

    @Input
    val module = project.name.toString()

    @OutputDirectory
    val outputDir = File(project.buildDir, "generated/vela/buildconfig/java")

    @OutputFile
    val outputFile = File(outputDir, "BuildConfig.java")

    @TaskAction
    fun generateBuildConfig() {
        SourceGenerator(
            outputFile,
            "BuildConfig",
            mapOf(
                "group" to projectGroup,
                "module" to module,
                "version" to version
            )
        )
    }
}