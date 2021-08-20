package de.menkalian.vela.gradle

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

open class BuildconfigGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.create("generateBuildConfig", BuildconfigTask::class.java)
    }
}

internal fun Project.sourceSets(): NamedDomainObjectContainer<SourceSet> =
    extensions.getByName("sourceSets") as SourceSetContainer
