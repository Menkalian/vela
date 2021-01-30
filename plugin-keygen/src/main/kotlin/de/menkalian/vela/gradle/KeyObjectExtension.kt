@file:Suppress("SpellCheckingInspection")

package de.menkalian.vela.gradle

import org.gradle.api.Project
import java.io.File
import java.net.URI

open class KeyObjectExtension internal constructor(buildDir: File, projectDir: File) {
    constructor(project: Project) : this(project.buildDir, project.projectDir)

    var targetDir: URI = File(buildDir, "generated/vela/keyobject/java").toURI()
    var sourceDir: URI = File(projectDir, "src/main/ckf").toURI()

    var separator = "."
    var targetPackage = "de.menkalian.vela.generated"

    var scanRecursive = false
    var prefixRecursive = false
    var finalLayerAsString = false
}