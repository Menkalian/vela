package de.menkalian.vela

import groovy.lang.Closure
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import java.io.File
import javax.inject.Inject
import kotlin.math.sqrt

open class BackgroundExtension @Inject constructor(val project: Project, objectFactory: ObjectFactory) {
    var seed: Long? = null
    var generationTarget: File = File(project.buildDir, "generated/res/backgrounds/")
    val backgrounds: NamedDomainObjectContainer<BackgroundConfig> =
        objectFactory.domainObjectContainer(BackgroundConfig::class.java) { BackgroundConfig(it, project) }

    fun backgrounds(config: NamedDomainObjectContainer<BackgroundConfig>.() -> Unit) = backgrounds.config()
    fun backgrounds(config: Closure<*>) = project.configure(backgrounds, config)
}

open class BackgroundConfig(val name: String, val project: Project) {
    var widthToHeightRatio: Double = 9.0 / 16.0
    var backgroundBaseColor: String = "#000000"

    var foregroundObjectsAmount: Int = 50
    var foregroundObjectsShape: String = "star"
    var foregroundObjectSizeRange: IntRange = 1..5
    var foregroundObjectsColor: String = "#FFFFFF"

    var verticalMovementAnimation: MovementAnimationConfig = MovementAnimationConfig()
    var horizontalMovementAnimation: MovementAnimationConfig = MovementAnimationConfig()
    var colorAnimation: ColorAnimationConfig = ColorAnimationConfig()
    var sizeAnimation: SizeAnimationConfig = SizeAnimationConfig()
    var opacityAnimation: OpacityAnimationConfig = OpacityAnimationConfig()

    fun verticalMovementAnimation(config: MovementAnimationConfig.() -> Unit) = verticalMovementAnimation.config()
    fun verticalMovementAnimation(config: Closure<*>): MovementAnimationConfig = project.configure(verticalMovementAnimation, config) as MovementAnimationConfig

    fun horizontalMovementAnimation(config: MovementAnimationConfig.() -> Unit) = horizontalMovementAnimation.config()
    fun horizontalMovementAnimation(config: Closure<*>): MovementAnimationConfig =
        project.configure(horizontalMovementAnimation, config) as MovementAnimationConfig

    fun colorAnimation(config: ColorAnimationConfig.() -> Unit) = colorAnimation.config()
    fun colorAnimation(config: Closure<*>): MovementAnimationConfig = project.configure(colorAnimation, config) as MovementAnimationConfig

    fun sizeAnimation(config: SizeAnimationConfig.() -> Unit) = sizeAnimation.config()
    fun sizeAnimation(config: Closure<*>): MovementAnimationConfig = project.configure(sizeAnimation, config) as MovementAnimationConfig

    fun opacityAnimation(config: OpacityAnimationConfig.() -> Unit) = opacityAnimation.config()
    fun opacityAnimation(config: Closure<*>): MovementAnimationConfig = project.configure(opacityAnimation, config) as MovementAnimationConfig
}

open class MovementAnimationConfig : AnimationConfig() {
    var direction: Int = 0
    var speed: Double = 0.0
}

open class SizeAnimationConfig : AnimationConfig() {
    var scaleFactor: IntRange = 1..1
    var interpolator: String = "@android:interpolator/decelerate_cubic"
}

open class OpacityAnimationConfig : AnimationConfig() {
    var minValue: Double = 1.0
    var maxValue: Double = 1.0
}

open class ColorAnimationConfig : AnimationConfig() {
    var possibleColors: Array<String> = arrayOf("#FFFFFF")

    companion object {
        val ALL_COLORS: Array<String>
        val BRIGHT_COLORS: Array<String>

        init {
            val listOfAllColorValues = (0x000000..0xFFFFFF).toList()
            ALL_COLORS = listOfAllColorValues.map { String.format("#%6x", it) }.toTypedArray()
            BRIGHT_COLORS = listOfAllColorValues
                .filter {
                    val rValue = it.shr(16).and(0xFF)
                    val gValue = it.shr(8).and(0xFF)
                    val bValue = it.shr(0).and(0xFF)
                    sqrt(((rValue * rValue) + (gValue * gValue) + (bValue * bValue)).toDouble() / 3 * 255 * 255) > 0.8
                }
                .map { String.format("#%6x", it) }.toTypedArray()
        }
    }
}

open class AnimationConfig {
    var durationRange: IntRange = 300..300
    var repeat: Boolean = false
    var repeatMode: String = "reverse"
}