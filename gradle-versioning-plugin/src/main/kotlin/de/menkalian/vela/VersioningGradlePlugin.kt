package de.menkalian.vela

import com.android.build.api.dsl.ApplicationBaseFlavor
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class VersioningGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val versionPropsFile = target.file("version.properties")
        var versionBuild = 0

        if (!versionPropsFile.exists())
            versionPropsFile.createNewFile()

        fun autoIncrementBuildNumber() {
            if (versionPropsFile.canRead()) {
                val versionProps = java.util.Properties()
                versionProps.load(versionPropsFile.inputStream())
                versionBuild = versionProps.getProperty("VERSION_BUILD", "0").toInt() + 1
                versionProps["VERSION_BUILD"] = versionBuild.toString()
                versionProps.store(versionPropsFile.outputStream(), "Automatic Build Number. DO NOT ALTER!!!")
            }
        }

        target.gradle.taskGraph.whenReady {
            if (it.hasTask(target.tasks.getByName("assembleRelease")))
                autoIncrementBuildNumber()
        }

        if (versionPropsFile.canRead()) {
            val versionProps = java.util.Properties()
            versionProps.load(versionPropsFile.inputStream())
            versionBuild = versionProps.getProperty("VERSION_BUILD", "0").toInt()
        }

        target.pluginManager.withPlugin("com.android.application") {
            val androidFlavour = target.androidFlavour()
            if (androidFlavour.versionCode == 0) {
                androidFlavour.versionCode = versionBuild
            }

            androidFlavour.versionName = androidFlavour.versionName?.replace("{{BUILD_NO}}", versionBuild.toString()) ?: versionBuild.toString()
        }
    }
}

internal fun Project.androidFlavour(): ApplicationBaseFlavor<*> =
    ((extensions.getByName("android") as? CommonExtension<*, *, *, *, *, *, *, *>)!!.defaultConfig as ApplicationBaseFlavor<*>)
