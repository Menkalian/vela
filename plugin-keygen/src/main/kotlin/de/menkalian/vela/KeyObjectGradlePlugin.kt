package de.menkalian.vela

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import java.io.File

const val EXTENSION_NAME = "keygen"

@Suppress("unused")
class KeyObjectGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create(EXTENSION_NAME, KeyObjectExtension::class.java, target)
        val generationTask = target.tasks.create("generateKeyObjects", GenerationTask::class.java)

        target.pluginManager.withPlugin("java") {
            target.sourceSets().getByName("main").java.srcDir(target.keygenConfig().targetDir)
        }

        target.afterEvaluate {
            target.pluginManager.withPlugin("java") {
                target.tasks.getByName("compileJava").dependsOn(generationTask)
            }
            target.pluginManager.withPlugin("com.android.base") {
                target.buildTypeNames().forEach {
                    target.tasks.getByName("compile${it.capitalize()}Java").dependsOn.add(generationTask)
                }
                val generationBaseDir = File(target.keygenConfig().targetDir)
                generationBaseDir.mkdirs()
                (target.extensions.getByName("android") as? CommonExtension<*, *, *, *, *, *, *, *>)!!.sourceSets.getByName("main").java.srcDir(
                    generationBaseDir
                )
            }
        }
    }
}

internal fun Project.buildTypeNames(): Set<String> =
    ((extensions.getByName("android") as? CommonExtension<*, *, *, *, *, *, *, *>)!!.buildTypes.names)

internal fun Project.sourceSets(): NamedDomainObjectContainer<SourceSet> =
    extensions.getByName("sourceSets") as SourceSetContainer

internal fun Project.keygenConfig(): KeyObjectExtension {
    return extensions.getByName(EXTENSION_NAME) as KeyObjectExtension
}