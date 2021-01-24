@file:Suppress("EqualsOrHashCode")

package de.menkalian.vela

import groovy.lang.Closure
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import java.io.File
import javax.inject.Inject

open class BackgroundExtension @Inject constructor(val project: Project, objectFactory: ObjectFactory) {
    var seed: Long? = null
    var generationTarget: File = File(project.buildDir, "generated/res/backgrounds/")
    val backgrounds: NamedDomainObjectContainer<BackgroundConfig> =
        project.container(BackgroundConfig::class.java) { name -> BackgroundConfig(name, project) }

    fun backgrounds(config: NamedDomainObjectContainer<BackgroundConfig>.() -> Unit) = backgrounds.config()
    fun backgrounds(config: Closure<*>) = project.configure(backgrounds, config)

    override fun hashCode(): Int {
        var result = project.hashCode()
        result = 31 * result + (seed?.hashCode() ?: 0)
        result = 31 * result + generationTarget.hashCode()
        result = 31 * result + backgrounds.asMap.values.hashCode()
        return result
    }
}

open class BackgroundConfig(val name: String, val project: Project) {
    var widthToHeightRatio: Double = 9.0 / 16.0
    var backgroundBaseColor: String = "#000000"

    var foregroundObjectsAmount: Int = 50
    var foregroundObjectsShape: String = "star"
    var foregroundObjectsSizeRange: IntRange = 1..5
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

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + project.hashCode()
        result = 31 * result + widthToHeightRatio.hashCode()
        result = 31 * result + backgroundBaseColor.hashCode()
        result = 31 * result + foregroundObjectsAmount
        result = 31 * result + foregroundObjectsShape.hashCode()
        result = 31 * result + foregroundObjectsSizeRange.hashCode()
        result = 31 * result + foregroundObjectsColor.hashCode()
        result = 31 * result + verticalMovementAnimation.hashCode()
        result = 31 * result + horizontalMovementAnimation.hashCode()
        result = 31 * result + colorAnimation.hashCode()
        result = 31 * result + sizeAnimation.hashCode()
        result = 31 * result + opacityAnimation.hashCode()
        return result
    }


}

open class MovementAnimationConfig : AnimationConfig() {
    var direction: Int = 0
    var speed: Double = 0.0

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + direction
        result = 31 * result + speed.hashCode()
        return result
    }
}

open class SizeAnimationConfig : AnimationConfig() {
    var scaleFactor: IntRange = 1..1
    var interpolator: String = "@android:interpolator/decelerate_cubic"

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + scaleFactor.hashCode()
        result = 31 * result + interpolator.hashCode()
        return result
    }
}

open class OpacityAnimationConfig : AnimationConfig() {
    var minValue: Double = 1.0
    var maxValue: Double = 1.0

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + minValue.hashCode()
        result = 31 * result + maxValue.hashCode()
        return result
    }

}

open class ColorAnimationConfig : AnimationConfig() {
    var possibleColors: Array<String> = arrayOf("#FFFFFF")

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + possibleColors.contentHashCode()
        return result
    }
}

open class AnimationConfig {
    var durationRange: IntRange = 300..300
    var repeat: Boolean = false
    var repeatMode: String = "reverse"

    override fun hashCode(): Int {
        var result = durationRange.hashCode()
        result = 31 * result + repeat.hashCode()
        result = 31 * result + repeatMode.hashCode()
        return result
    }
}