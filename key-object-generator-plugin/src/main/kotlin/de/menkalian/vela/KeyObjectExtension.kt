package de.menkalian.vela

import org.gradle.api.Project
import java.io.File

open class KeyObjectExtension(project: Project) {
    var targetDir = File(project.buildDir, "generated/keyobjects")
    var baseDir = File(project.rootDir, "keys")
    var scanRecursive = false
    var prefixRecursive = false
}