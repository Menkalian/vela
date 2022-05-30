@file:Suppress("SpellCheckingInspection")

package de.menkalian.vela.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.io.File
import javax.inject.Inject

@Suppress("LeakingThis")
abstract class KeygenExtension @Inject internal constructor(project: Project, val name: String) {
    companion object {
        /**
         * Constant name of the container which contains these configurations
         */
        const val CONTAINER_NAME = "keygen"
    }

    /**
     * Target directory where the generated sources are put
     */
    abstract val targetDir: Property<File>

    /**
     * Source directory where the keys are read from
     */
    abstract val sourceDir: Property<File>

    /**
     * Separator between the layers of the key
     */
    abstract val separator: Property<String>

    /**
     * Target package for the generated sources
     */
    abstract val targetPackage: Property<String>

    /**
     * Generator type to use
     */
    abstract val generator: Property<Generator>

    /**
     * Whether to scan the source directory recursively
     */
    abstract val scanRecursive: Property<Boolean>

    /**
     * Whether to prefix keys from subdirectories according to the directory hierarchy
     */
    abstract val prefixRecursive: Property<Boolean>

    /**
     * Whether to generate the final layer plainly as a string object
     */
    abstract val finalLayerAsString: Property<Boolean>

    init {
        targetDir.convention(File(project.buildDir, "generated/vela/keygen/$name"))
        sourceDir.convention(File(project.projectDir, "src/main/keygen"))

        generator.convention(Generator.DEFAULT)
        separator.convention(".")
        targetPackage.convention(project.group.toString())

        scanRecursive.convention(true)
        prefixRecursive.convention(false)
        finalLayerAsString.convention(false)
    }

    /**
     * Enumeration of the available Generator types
     *
     * @property JAVA Generate Java Sources
     * @property KOTLIN Generate Kotlin Sources
     * @property DEFAULT Use the type which best matches the current project
     */
    enum class Generator {
        JAVA,
        KOTLIN,
        DEFAULT;

        companion object {
            /**
             * Property to determine the best fitted generator type for the current project
             */
            internal var defaultGenerator: Generator = JAVA
        }
    }
}