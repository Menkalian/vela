@file:Suppress("SpellCheckingInspection")

package de.menkalian.vela.gradle

import org.gradle.api.Project
import java.io.File
import java.net.URI

open class FeaturetoggleExtension internal constructor(buildDir: File, projectDir: File, val project: Project? = null) {
    constructor(project: Project) : this(project.buildDir, project.projectDir, project)

    enum class EGeneratorImplementation {
        DEFAULT,
        KOTLIN
    }

    var generator: EGeneratorImplementation = EGeneratorImplementation.DEFAULT

    var targetDir: URI = File(buildDir, "generated/vela/featuretoggle/kotlin").toURI()
    var sourceFile: URI = File(projectDir, "src/main/resources/features.xml").toURI()

    var targetPackage = "de.menkalian.vela.generated"
    var mutableAfterInitialization = false
}