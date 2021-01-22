package de.menkalian.vela

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlin.random.Random

class VersioningGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.withPlugin("com.android.base") {
            val backgroundGenerator = target.tasks.create("generateBackgroundResources", BackgroundGenerator::class.java)

            target.buildTypeNames().forEach {
                target.task("generate${it.capitalize()}Resources").dependsOn.add(backgroundGenerator)
            }

            val generationBaseDir = File(target.buildDir, "generated/background_res/")
            generationBaseDir.mkdirs()
            backgroundGenerator.generationDir = generationBaseDir

            (target.extensions.getByName("android") as? CommonExtension<*, *, *, *, *, *, *, *>)!!.sourceSets.getByName("main").res.srcDir(generationBaseDir)
        }
    }
}

internal fun Project.buildTypeNames(): Set<String> =
    ((extensions.getByName("android") as? CommonExtension<*, *, *, *, *, *, *, *>)!!.buildTypes.names)


class BackgroundGenerator : DefaultTask() {
    lateinit var generationDir: File

    @TaskAction
    fun generateBackground() {
        val rng = Random.Default

        val listValues = mutableListOf<IntArray>()

        for (i in 1..50) {
            val posX = rng.nextInt(0, 900)
            val posY = rng.nextInt(0, 1600)
            val radius = rng.nextInt(5)

            listValues.add(intArrayOf(posX, posY, radius))
        }

        val bgFile = File(generationDir, "drawable/aquila_star_bg.xml")
        bgFile.createNewFile()
        bgFile.writeText(
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<vector xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                    "        android:height=\"160dp\"\n" +
                    "        android:width=\"90dp\"\n" +
                    "        android:viewportHeight=\"1600\"\n" +
                    "        android:viewportWidth=\"900\">\n" +
                    "    <path\n" +
                    "            android:fillColor=\"@color/menu_base_background\"\n" +
                    "            android:strokeWidth=\"0\"\n" +
                    "            android:pathData=\"M0,0 900,0 900,1600 0,1600z\" />\n"
        )
        logger.info("Generating VectorGraphic...")
        listValues.forEachIndexed { index, values ->
            val baseOffset = (1.0 / 3.0) * values[2].toDouble()
            val extensionOffset = (2.0 / 3.0) * values[2].toDouble()

            val xCenter = values[0].toDouble()
            val xValues = doubleArrayOf(
                xCenter - baseOffset,
                xCenter,
                xCenter + baseOffset,
                xCenter + baseOffset + extensionOffset,
                xCenter + baseOffset,
                xCenter,
                xCenter - baseOffset,
                xCenter - baseOffset - extensionOffset
            )

            val yCenter = values[1].toDouble()
            val yValues = doubleArrayOf(
                yCenter - baseOffset,
                yCenter - baseOffset - extensionOffset,
                yCenter - baseOffset,
                yCenter,
                yCenter + baseOffset,
                yCenter + baseOffset + extensionOffset,
                yCenter + baseOffset,
                yCenter
            )

            val pathData = StringBuilder()
            pathData.append("M")
            for (i in xValues.indices) {
                pathData.append("${xValues[i]},${yValues[i]}")
                if (i + 1 != xValues.size)
                    pathData.append(" ")
            }
            pathData.append("z")

            bgFile.appendText(
                "        <path\n" +
                        "        android:name=\"star${String.format("%02d", index)}\"\n" +
                        "        android:strokeColor=\"@color/menu_base_stars\"\n" +
                        "        android:strokeWidth=\"${values[2]}\"\n" +
                        "        android:pathData=\"$pathData\"/>\n"
            )
        }

        bgFile.appendText("</vector>")

        logger.info("Generating AnimatedVectorGraphic...")
        val animatedBgFile = File(generationDir, "drawable/aquila_star_bg_animated.xml")
        animatedBgFile.createNewFile()
        animatedBgFile.writeText(
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<animated-vector xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                    "  android:drawable=\"@drawable/aquila_star_bg\" >\n"
        )
        listValues.forEachIndexed { index, values ->
            animatedBgFile.appendText(
                "<target\n" +
                        "        android:name=\"star${String.format("%02d", index)}\"\n" +
                        "        android:animation=\"@animator/star_animator_${String.format("%02d", index)}\" />\n"
            )
        }
        animatedBgFile.appendText("</animated-vector>")


        logger.info("Generating Animators...")
        val animatorDir = File(generationDir, "animator")
        animatorDir.mkdir()
        listValues.forEachIndexed { index, values ->
            val animatorFile = File(animatorDir, "star_animator_${String.format("%02d", index)}.xml")
            animatorFile.createNewFile()
            animatorFile.writeText(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<set xmlns:android=\"http://schemas.android.com/apk/res/android\">\n"
            )
            animatorFile.appendText(
                "        <objectAnimator\n" +
                        "        android:duration=\"${rng.nextInt(20) * 100}\"\n" +
                        "        android:propertyName=\"strokeWidth\"\n" +
                        "        android:valueFrom=\"${values[2]}\"\n" +
                        "        android:valueTo=\"${rng.nextInt(4, 7) * values[2]}\"\n" +
                        "        android:repeatMode=\"reverse\" />\n"
            )
            animatorFile.appendText("</set>")
        }
    }
}
