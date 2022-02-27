package de.menkalian.vela.gradle

import de.menkalian.vela.featuretoggle.compiler.CompileConfig
import de.menkalian.vela.featuretoggle.compiler.ICompileChain
import de.menkalian.vela.featuretoggle.compiler.backend.Generators
import de.menkalian.vela.gradle.FeaturetoggleExtension.EGeneratorImplementation.DEFAULT
import de.menkalian.vela.gradle.FeaturetoggleExtension.EGeneratorImplementation.KOTLIN
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class FeaturetoggleGenerationTask: DefaultTask() {
    private fun getConfig() = project.featuretoggleConfig()

    init {
        group = "vela"
    }

    @InputFile
    fun getInputFile(): File = File(getConfig().sourceFile)

    @OutputDirectory
    fun getTargetDir(): File = File(getConfig().targetDir)

    @Input
    fun getGenerator() = getConfig().generator.name

    @Input
    fun getTargetPackage() = getConfig().targetPackage

    @Input
    fun isMutableAfterInitialization() = getConfig().mutableAfterInitialization

    @TaskAction
    fun generateFeaturetoggleSources() {
        val generator = when(getConfig().generator) {
            DEFAULT, KOTLIN -> Generators.KOTLIN
        }

        CompileConfig.mutabilityEnabled = isMutableAfterInitialization()
        ICompileChain
            .create(getInputFile(), generator = generator)
            .compile(getTargetDir(), getTargetPackage())
    }
}