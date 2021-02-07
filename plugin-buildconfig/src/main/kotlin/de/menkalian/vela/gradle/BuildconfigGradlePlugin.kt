package de.menkalian.vela.gradle

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

open class BuildconfigGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val generationTask = target.tasks.create("generateBuildConfig", BuildconfigTask::class.java)

        target.afterEvaluate {
            target.pluginManager.withPlugin("java") {
                target.sourceSets().getByName("main").java.srcDir(generationTask.outputDir)

                target.tasks.getByName("compileJava").dependsOn(generationTask)
            }
            target.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
                target.tasks.getByName("compileKotlin").dependsOn(generationTask)
            }
        }
    }
}

internal fun Project.sourceSets(): NamedDomainObjectContainer<SourceSet> =
    extensions.getByName("sourceSets") as SourceSetContainer
