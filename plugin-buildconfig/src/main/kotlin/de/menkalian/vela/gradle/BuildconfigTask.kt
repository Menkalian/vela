package de.menkalian.vela.gradle

import de.menkalian.vela.plain.SourceGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class BuildconfigTask : DefaultTask() {
    init {
        group = "vela"
        outputs.upToDateWhen {
            false
        }

        project.afterEvaluate {
            project.pluginManager.withPlugin("java") {
                project.sourceSets().getByName("main").java.srcDir(getOutputDir())

                project.tasks.getByName("compileJava").dependsOn(this)
            }
            project.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
                project.tasks.getByName("compileKotlin").dependsOn(this)
            }

        }
    }

    private fun getOutputDir() = File(project.buildDir, "generated/vela/buildconfig/java")
    private fun getOutputFile() = File(getOutputDir(), "${project.group.toString().replace(".", "/")}/BuildConfig.java")

    @TaskAction
    fun generateBuildConfig() {
        val outputDir = getOutputDir()
        if (outputDir.exists())
            outputDir.deleteRecursively()
        SourceGenerator(
            getOutputFile(),
            "BuildConfig",
            mapOf(
                "group" to project.group.toString(),
                "module" to project.name.toString(),
                "version" to project.version.toString()
            )
        )
    }
}