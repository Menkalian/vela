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
    fun version() = project.version.toString()

    @Input
    fun group() = project.group.toString()

    @Input
    fun module() = project.name.toString()

    @OutputDirectory
    fun outputDir() =
        File(project.buildDir, "generated/vela/buildconfig/java")

    @OutputFile
    fun outputFile() = File(outputDir(), "BuildConfig.java")

    @TaskAction
    fun generateBuildConfig() {
        SourceGenerator(
            outputFile(),
            "BuildConfig",
            mapOf(
                "group" to group(),
                "module" to module(),
                "version" to version()
            )
        )
    }
}