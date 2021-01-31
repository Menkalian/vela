@file:Suppress("SpellCheckingInspection")

package de.menkalian.vela.gradle

import groovy.lang.Closure
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import java.io.File
import java.net.URI

open class KeyObjectExtension internal constructor(buildDir: File, projectDir: File, val project: Project? = null, val name: String = "keygen") {
    constructor(project: Project) : this(project.buildDir, project.projectDir, project)
    constructor(name: String, project: Project) : this(project.buildDir, project.projectDir, project, name)

    var targetDir: URI = File(buildDir, "generated/vela/keyobject/java").toURI()
    var sourceDir: URI = File(projectDir, "src/main/ckf").toURI()

    var separator = "."
    var targetPackage = "de.menkalian.vela.generated"

    var scanRecursive = false
    var prefixRecursive = false
    var finalLayerAsString = false

    val furtherConfigs: NamedDomainObjectContainer<KeyObjectExtension>? = project?.container(KeyObjectExtension::class.java) { name ->
        KeyObjectExtension(
            name,
            project
        )
    }

    fun furtherConfigs(config: NamedDomainObjectContainer<KeyObjectExtension>.() -> Unit) = furtherConfigs?.config()
    fun furtherConfigs(config: Closure<*>) = project?.configure(furtherConfigs!!, config)
}