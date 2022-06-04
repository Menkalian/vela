@file:Suppress("UnstableApiUsage")

package de.menkalian.vela.gradle

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.targets

/**
 * Main class of the plugin
 */
@Suppress("unused")
open class KeygenGradlePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val objectContainer = target.container(KeygenExtension::class.java) { name ->
            target.objects.newInstance(KeygenExtension::class.java, target, name)
        }
        target.extensions.add(KeygenExtension.CONTAINER_NAME, objectContainer)

        val keygenTask = target.tasks.create("generateKeyObjects", KeygenTask::class.java)

        target.afterEvaluate {
            // Determine the best generator type
            if (target.pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")
                || target.pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")
                || target.pluginManager.hasPlugin("org.jetbrains.kotlin.android")
            ) {
                KeygenExtension.Generator.defaultGenerator =
                    KeygenExtension.Generator.KOTLIN
            } else {
                KeygenExtension.Generator.defaultGenerator =
                    KeygenExtension.Generator.JAVA
            }

            // Configure compileTasks to run after the generation
            target.pluginManager.withPlugin("java") {
                target.tasks.getByName("compileJava").dependsOn(keygenTask)
            }
            target.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
                target.tasks.getByName("compileKotlin").dependsOn(keygenTask)
            }
            target.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
                target.tasks.withType(KotlinCompile::class.java)
                    .configureEach {
                        it.dependsOn(keygenTask)
                    }
            }
            target.pluginManager.withPlugin("com.android.base") {
                target.androidBuildTypeNames().forEach {
                    target.tasks.getByName("compile${it.capitalized()}Java").dependsOn(keygenTask)
                }
            }

            // Add source directories
            val sourceDirectories = objectContainer
                .asIterable()
                .flatMap {
                    listOf(
                        it.targetDir.get().absolutePath + "/java",
                        it.targetDir.get().absolutePath + "/kotlin",
                    )
                }.toTypedArray()

            target.pluginManager.withPlugin("java") {
                target.afterEvaluate {
                    // Only register without multiplatform
                    if (target.pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform").not()) {
                        target.sourceSets()
                            .getByName("main").java
                            .srcDirs(*sourceDirectories)
                    }
                }
            }
            target.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
                target.afterEvaluate {
                    // Only register without multiplatform
                    if (target.pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform").not()) {
                        target.sourceSets()
                            .getByName("main").java
                            .srcDirs(*sourceDirectories)
                    }
                }
            }
            target.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
                target.kotlinCommonCodeSourceSet()?.kotlin
                    ?.srcDirs(*sourceDirectories)
            }

            target.pluginManager.withPlugin("com.android.base") {
                target.androidSourceSets().getByName("main")
                    .java.srcDirs(*sourceDirectories)
            }
        }
    }

    /**
     * Utility method to obtain the SourceSetContainer for a project
     */
    private fun Project.sourceSets(): NamedDomainObjectContainer<SourceSet> =
        extensions.getByName("sourceSets") as SourceSetContainer

    /**
     * Utility method to obtain the SourceSetContainer of the Kotlin (Multiplatform) plugin
     */
    private fun Project.kotlinSourceSets(): NamedDomainObjectContainer<KotlinSourceSet> =
        kotlinExtension.sourceSets

    /**
     * Utility method to try to find the main shared SourceSet of a Kotlin (Multiplatform) project
     */
    private fun Project.kotlinCommonCodeSourceSet(): KotlinSourceSet? {
        val commonMain = kotlinSourceSets().findByName("commonMain")
        if (commonMain != null) {
            return commonMain
        }

        // Find first common code source set
        return project.kotlinExtension.targets
            .firstOrNull {
                it.platformType == KotlinPlatformType.common
            }?.compilations?.firstOrNull()?.defaultSourceSet
    }

    /**
     * Utility method to obtain the SourceSetContainer of the Android plugin
     */
    private fun Project.androidSourceSets(): NamedDomainObjectContainer<out AndroidSourceSet> =
        (extensions.getByName("android") as? CommonExtension<*, *, *, *>)!!.sourceSets

    /**
     * Utility method to obtain the BuildTypes of the Android plugin
     */
    private fun Project.androidBuildTypeNames(): Set<String> =
        (extensions.getByName("android") as? CommonExtension<*, *, *, *>)!!.buildTypes.names
}
