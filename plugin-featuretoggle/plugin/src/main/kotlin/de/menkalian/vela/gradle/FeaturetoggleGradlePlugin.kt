@file:Suppress("UnstableApiUsage")

package de.menkalian.vela.gradle

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import java.io.File

const val EXTENSION_NAME = "featuretoggle"

@Suppress("unused")
class FeaturetoggleGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create(EXTENSION_NAME, FeaturetoggleExtension::class.java, target)
        val generationTask = target.tasks.create("generateFeaturetoggleCode", FeaturetoggleGenerationTask::class.java)

        target.pluginManager.withPlugin("java") {
            target.sourceSets().getByName("main").java.srcDir(target.featuretoggleConfig().targetDir)
            target.tasks.getByName("compileJava").dependsOn(generationTask)
        }

        target.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            target.tasks.getByName("compileKotlin").dependsOn(generationTask)
        }

        target.pluginManager.withPlugin("com.android.base") {
            target.buildTypeNames().forEach {
                target.tasks.getByName("compile${it.capitalize()}Java").dependsOn.add(generationTask)
                target.tasks.getByName("compile${it.capitalize()}Kotlin").dependsOn.add(generationTask)
            }
            val generationBaseDir = File(target.featuretoggleConfig().targetDir)
            generationBaseDir.mkdirs()
            (target.extensions.getByName("android") as? CommonExtension<*, *, *, *, *, *, *, *>)!!.sourceSets.getByName("main").java.srcDir(
                generationBaseDir
            )
        }
    }
}

internal fun Project.buildTypeNames(): Set<String> =
    ((extensions.getByName("android") as? CommonExtension<*, *, *, *, *, *, *, *>)!!.buildTypes.names)

internal fun Project.sourceSets(): NamedDomainObjectContainer<SourceSet> =
    extensions.getByName("sourceSets") as SourceSetContainer

internal fun Project.featuretoggleConfig(): FeaturetoggleExtension {
    return extensions.getByName(EXTENSION_NAME) as FeaturetoggleExtension
}
