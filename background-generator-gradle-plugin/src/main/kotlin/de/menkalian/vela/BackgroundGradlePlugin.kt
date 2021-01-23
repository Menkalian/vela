package de.menkalian.vela

import com.android.build.api.dsl.ApplicationBaseFlavor
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlin.math.roundToInt
import kotlin.random.Random

class BackgroundGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create("backgrounds", BackgroundExtension::class.java, target)

        target.pluginManager.withPlugin("com.android.base") {
            val backgroundGenerator = target.tasks.create("generateBackgroundResources", BackgroundGenerator::class.java)

            target.afterEvaluate {
                target.buildTypeNames().forEach {
                    target.tasks.getByName("generate${it.capitalize()}Resources").dependsOn.add(backgroundGenerator)
                }
                val generationBaseDir = target.backgrounds().generationTarget
                generationBaseDir.mkdirs()
                (target.extensions.getByName("android") as? CommonExtension<*, *, *, *, *, *, *, *>)!!.sourceSets.getByName("main").res.srcDir(generationBaseDir)
            }
        }
    }
}

internal fun Project.buildTypeNames(): Set<String> =
    ((extensions.getByName("android") as? CommonExtension<*, *, *, *, *, *, *, *>)!!.buildTypes.names)


internal fun Project.buildCodeNumber(): Long {
    return try {
        ((extensions.getByName("android") as? CommonExtension<*, *, *, *, *, *, *, *>)!!.defaultConfig as ApplicationBaseFlavor<*>).versionCode!!.toLong()
    } catch (ex: Exception) {
        Random.nextLong()
    }
}

internal fun Project.backgrounds(): BackgroundExtension =
    extensions.getByName("backgrounds") as BackgroundExtension


open class BackgroundGenerator : DefaultTask() {
    lateinit var generationDir: File

    @Input
    fun getExtensionConfiguration(): BackgroundExtension = project.backgrounds()

    @Input
    fun getSeed(): Long = project.backgrounds().seed ?: project.buildCodeNumber()

    @OutputDirectory
    fun getOutputDir(): File = project.backgrounds().generationTarget

    @TaskAction
    fun generateBackgrounds() {
        val rng = Random(getSeed())

        getExtensionConfiguration().backgrounds.forEach { backgroundConfig ->
            val listValues = mutableListOf<IntArray>()

            val (width: Int, heigth: Int) = getWidthAndHeight(backgroundConfig)

            for (i in 1..backgroundConfig.foregroundObjectsAmount) {
                val posX = rng.nextInt(0, width * 10)
                val posY = rng.nextInt(0, heigth * 10)

                val radius = rng.nextInt(
                    backgroundConfig.foregroundObjectSizeRange.first,
                    backgroundConfig.foregroundObjectSizeRange.last
                )

                listValues.add(intArrayOf(posX, posY, radius))

            }

            generateStaticVectorDrawable(listValues, backgroundConfig)
            generateAnimatedVectorDrawable(listValues, backgroundConfig)
            generateAnimators(listValues, rng, backgroundConfig)
        }
    }

    private fun getWidthAndHeight(backgroundConfig: BackgroundConfig): Pair<Int, Int> {
        val width: Int
        val heigth: Int
        if (backgroundConfig.widthToHeightRatio > 1) {
            width = 200
            heigth = (200 / backgroundConfig.widthToHeightRatio).roundToInt()
        } else {
            heigth = 200
            width = (200 * backgroundConfig.widthToHeightRatio).roundToInt()
        }
        return Pair(width, heigth)
    }

    private fun generateStaticVectorDrawable(listValues: MutableList<IntArray>, backgroundConfig: BackgroundConfig) {
        val (width: Int, heigth: Int) = getWidthAndHeight(backgroundConfig)

        val bgFile = File(generationDir, "drawable/${backgroundConfig.name}.xml")
        bgFile.parentFile.mkdirs()
        bgFile.createNewFile()
        bgFile.writeText(
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<vector xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                    "        android:height=\"${heigth}dp\"\n" +
                    "        android:width=\"${width}dp\"\n" +
                    "        android:viewportHeight=\"${heigth * 10}\"\n" +
                    "        android:viewportWidth=\"${width * 10}\">\n" +
                    "    <path\n" +
                    "            android:fillColor=\"${backgroundConfig.backgroundBaseColor}\"\n" +
                    "            android:strokeWidth=\"0\"\n" +
                    "            android:pathData=\"M0,0 ${width * 10},0 ${width * 10},${heigth * 10} 0,${heigth * 10}\" />\n"
        )
        logger.info("Generating VectorGraphic...")
        listValues.forEachIndexed { index, values ->
            val xCenter = values[0].toDouble()
            val yCenter = values[1].toDouble()

            val (xValues, yValues) = buildValueData(backgroundConfig.foregroundObjectsShape, xCenter, yCenter, values[2])

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
                        "        android:name=\"foreground_${backgroundConfig.name}_${String.format("%02d", index)}\"\n" +
                        "        android:strokeColor=\"${backgroundConfig.foregroundObjectsColor}\"\n" +
                        "        android:strokeWidth=\"${values[2]}\"\n" +
                        "        android:pathData=\"$pathData\"/>\n"
            )
        }

        bgFile.appendText("</vector>")
    }

    private fun buildValueData(shape: String, xCenter: Double, yCenter: Double, size: Int): Pair<DoubleArray, DoubleArray> {
        val xValues: DoubleArray
        val yValues: DoubleArray
        when (shape) {
            "star"          -> {
                val baseOffset = (1.0 / 3.0) * size.toDouble()

                xValues = doubleArrayOf(
                    xCenter - baseOffset,
                    xCenter,
                    xCenter + baseOffset,
                    xCenter + size,
                    xCenter + baseOffset,
                    xCenter,
                    xCenter - baseOffset,
                    xCenter - size
                )

                yValues = doubleArrayOf(
                    yCenter - baseOffset,
                    yCenter - size,
                    yCenter - baseOffset,
                    yCenter,
                    yCenter + baseOffset,
                    yCenter + size,
                    yCenter + baseOffset,
                    yCenter
                )
            }

            else /*square*/ -> {
                val baseOffset = (1.0 / 3.0) * size.toDouble()

                xValues = doubleArrayOf(
                    xCenter - baseOffset,
                    xCenter + baseOffset,
                    xCenter + baseOffset,
                    xCenter - baseOffset
                )

                yValues = doubleArrayOf(
                    yCenter - baseOffset,
                    yCenter - baseOffset,
                    yCenter + baseOffset,
                    yCenter + baseOffset
                )
            }
        }
        return Pair(xValues, yValues)
    }

    private fun generateAnimatedVectorDrawable(listValues: MutableList<IntArray>, backgroundConfig: BackgroundConfig) {
        logger.info("Generating AnimatedVectorGraphic...")
        val animatedBgFile = File(generationDir, "drawable/${backgroundConfig.name}_animated.xml")
        animatedBgFile.createNewFile()
        animatedBgFile.writeText(
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<animated-vector xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                    "  android:drawable=\"@drawable/${backgroundConfig.name}\" >\n"
        )
        listValues.forEachIndexed { index, values ->
            animatedBgFile.appendText(
                "<target\n" +
                        "        android:name=\"foreground_${backgroundConfig.name}_${String.format("%02d", index)}\"\n" +
                        "        android:animation=\"@animator/${backgroundConfig.name}_animator_${String.format("%02d", index)}\" />\n"
            )
        }
        animatedBgFile.appendText("</animated-vector>")
    }

    private fun generateAnimators(listValues: MutableList<IntArray>, rng: Random, backgroundConfig: BackgroundConfig) {
        logger.info("Generating Animators...")
        val animatorDir = File(generationDir, "animator")
        animatorDir.mkdirs()

        listValues.forEachIndexed { index, values ->
            val animatorFile = File(animatorDir, "${backgroundConfig.name}_animator_${String.format("%02d", index)}.xml")
            animatorFile.createNewFile()
            animatorFile.writeText(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<set xmlns:android=\"http://schemas.android.com/apk/res/android\">\n"
            )

            // add Size Animator
            val sizeConfig = backgroundConfig.sizeAnimation
            animatorFile.appendText(
                "        <objectAnimator\n" +
                        "        android:duration=\"${rng.nextInt(sizeConfig.durationRange.first, sizeConfig.durationRange.last)}\"\n" +
                        "        android:propertyName=\"strokeWidth\"\n" +
                        "        android:interpolator=\"${sizeConfig.interpolator}\"\n" +
                        "        android:valueFrom=\"${values[2]}\"\n" +
                        "        android:valueTo=\"${rng.nextInt(sizeConfig.scaleFactor.first, sizeConfig.scaleFactor.last) * values[2]}\"\n" +
                        "        android:valueType=\"floatType\"\n" +
                        "        android:repeatCount=\"${if (sizeConfig.repeat) "infinite" else "0"}\"\n" +
                        "        android:repeatMode=\"${sizeConfig.repeatMode}\" />\n"
            )

            animatorFile.appendText("</set>")
        }
    }
}
