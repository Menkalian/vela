package de.menkalian.vela.gradle

import de.menkalian.vela.plain.KeyGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class KeyObjectGenerationTask : DefaultTask() {
    private fun getConfig() = project.keygenConfig()

    init {
        group = "vela"
    }

    //region Inputs
    @InputDirectory
    fun getInputDir(): File = File(getConfig().sourceDir)

    @Input
    fun getSeparator() = getConfig().separator

    @Input
    fun getTargetPackage() = getConfig().targetPackage

    @Input
    fun isScanRecursive() = getConfig().scanRecursive

    @Input
    fun isPrefixRecursive() = getConfig().prefixRecursive

    @Input
    fun isFinalLayerAsString() = getConfig().finalLayerAsString
    //endregion Inputs

    //region Outputs
    @OutputDirectory
    fun getTargetDir(): File = File(getConfig().targetDir)
    //endregion Outputs

    @TaskAction
    fun generateKeySources() {
        KeyGenerator(getConfig()).runGeneration()

        getConfig().furtherConfigs?.forEach {
            KeyGenerator(it).runGeneration()
        }
    }
}