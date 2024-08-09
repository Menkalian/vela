package de.menkalian.vela.gradle

import de.menkalian.vela.gradle.BuildconfigExtension.Generator.*
import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.io.File

/**
 * Extension to configure the behaviour of the plugin
 */
abstract class BuildconfigExtension(project: Project) {
    companion object {
        /**
         * Constant Name which is used to register (and find) the extension
         */
        const val EXTENSION_NAME = "velaBuildconfig"
    }

    /**
     * Target directory where the generated sources are put.
     */
    abstract val targetDir: Property<File>

    /**
     * Target package for the generated sources
     */
    abstract val targetPackage: Property<String>

    /**
     * Generator type to use
     */
    abstract val generator: Property<Generator>

    /**
     * Properties to put inside the BuildConfig.
     * The initial value contains three properties: group, name and version of the gradle project
     */
    val additionalProperties: MutableMap<String, (Project) -> String> = mutableMapOf()

    init {
        targetDir.convention(File(project.buildDir.absolutePath + "/generated/vela/buildconfig"))
        targetPackage.convention(project.group.toString())
        generator.convention(Generator.DEFAULT)

        additionalProperties["group"] = { it.group.toString() }
        additionalProperties["module"] = { it.name.toString() }
        additionalProperties["version"] = { it.version.toString() }
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