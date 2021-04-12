package de.menkalian.vela.gradle

import de.menkalian.vela.determining.DatabaseVersionDeterminer
import de.menkalian.vela.determining.PropFileVersionDeterminer
import de.menkalian.vela.determining.VersionDeterminer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

@Suppress("unused")
class VersioningGradlePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val projKey = "${target.group}:${target.name}"
        val determiner: VersionDeterminer = DatabaseVersionDeterminer(projKey, PropFileVersionDeterminer(target.projectDir))

        val extension = target.extensions.create("versioning", VersioningExtension::class.java)

        extension.buildNo = determiner.getBuildNo() + 1
        target.afterEvaluate {
            target.gradle.taskGraph.whenReady {
                val upgradeTask: Task = if (extension.upgradeTask == null) {
                    target.tasks.findByName("build") ?: target.gradle.taskGraph.allTasks.first()
                } else {
                    extension.upgradeTask!!
                }

                if (target.gradle.taskGraph.hasTask(upgradeTask) && System.getenv().containsKey("VELA_NO_UPGRADE").not())
                    determiner.increaseBuildNo()
            }
        }
    }
}

open class VersioningExtension {
    var upgradeTask: Task? = null
    var buildNo: Int = 0
}
