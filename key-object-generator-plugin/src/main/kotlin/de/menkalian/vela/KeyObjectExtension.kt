package de.menkalian.vela

import org.gradle.api.Project
import java.io.File
import java.net.URI

open class KeyObjectExtension(project: Project) {
    var targetDir: URI = File(project.buildDir, "generated/vela/keyobject/java").toURI()
    var sourceDir: URI = File(project.projectDir, "src/main/ckf").toURI()

    var separator = "."
    var targetPackage = "de.menkalian.vela.generated"

    var scanRecursive = false
    var prefixRecursive = false
    var finalLayerAsString = false
}