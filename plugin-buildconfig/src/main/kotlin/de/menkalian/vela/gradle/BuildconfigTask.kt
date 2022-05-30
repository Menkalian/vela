package de.menkalian.vela.gradle

import de.menkalian.vela.template.evaluator.ITemplateEvaluator
import de.menkalian.vela.template.parser.ITemplateParser
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Task to generate the BuildConfig-Sources
 */
open class BuildconfigTask : DefaultTask() {
    init {
        group = "generate"
        outputs.upToDateWhen {
            false
        }
    }

    /**
     * Main Action of the task.
     * It clears the existing files and generates new ones in place of the previous ones.
     */
    @TaskAction
    fun generateBuildConfig() {
        val targetDir = File(getTargetDir())
        val evaluator = loadTemplate()

        if (targetDir.exists()) {
            targetDir.deleteRecursively()
        }
        targetDir.mkdirs()

        val variables = hashMapOf<String, String>()
        variables["Vela.Package"] = getBuildconfigExtension().targetPackage.get()
        variables["Vela.Properties.n"] = getBuildconfigExtension().additionalProperties.size.toString()

        getBuildconfigExtension().additionalProperties.onEachIndexed { idx, entry ->
            val pos = idx + 1
            variables["Vela.Properties.${pos.toString().padStart(3, '0')}.Name"] =
                entry.key
                    .replace("\"", "\\\"")
                    .replace("\$", "\\\$")

            variables["Vela.Properties.${pos.toString().padStart(3, '0')}.Value"] =
                entry.value(project)
                    .replace("\"", "\\\"")
                    .replace("\$", "\\\$")
        }

        val outFile = File(targetDir, getClassFile(getBuildconfigExtension().targetPackage.get().replace(".", "/") + "/BuildConfig"))
        outFile.parentFile.mkdirs()
        outFile.writeText(evaluator.evaluate(variables))
    }

    /**
     * Utility method to obtain the BuildconfigExtension
     */
    private fun getBuildconfigExtension(): BuildconfigExtension = project.extensions.getByType(BuildconfigExtension::class.java)

    /**
     * Get the selected/best generator for the project
     */
    private fun getGenerator(): BuildconfigExtension.Generator {
        val gen = getBuildconfigExtension().generator.get()
        if (gen == BuildconfigExtension.Generator.DEFAULT) {
            return BuildconfigExtension.Generator.defaultGenerator
        } else {
            return gen
        }
    }

    /**
     * Get the target dir for the generated sources
     */
    private fun getTargetDir(): String {
        return when (getGenerator()) {
            BuildconfigExtension.Generator.JAVA   -> getBuildconfigExtension().targetDir.get().path + "/java"
            BuildconfigExtension.Generator.KOTLIN -> getBuildconfigExtension().targetDir.get().path + "/kotlin"
            else                                  -> throw Exception("Invalid Generator Configuration")
        }
    }

    /**
     * Loads the template for the current generator
     */
    private fun loadTemplate(): ITemplateEvaluator {
        return ITemplateParser
            .getDefaultImplementation()
            .parse(
                this.javaClass.classLoader
                    .getResourceAsStream(getTemplatePath())!!
            )
    }

    /**
     * Determines which template to use for the current generator
     */
    private fun getTemplatePath(): String {
        return when (getGenerator()) {
            BuildconfigExtension.Generator.JAVA   -> "templates/java.vtp"
            BuildconfigExtension.Generator.KOTLIN -> "templates/kotlin.vtp"
            else                                  -> throw Exception("Invalid Generator Configuration")
        }
    }

    /**
     * Appends the correct filename-extension to the name of the generated file
     */
    private fun getClassFile(filename: String): String {
        return when (getGenerator()) {
            BuildconfigExtension.Generator.JAVA   -> "$filename.java"
            BuildconfigExtension.Generator.KOTLIN -> "$filename.kt"
            else                                  -> throw Exception("Invalid Generator Configuration")
        }
    }
}