package de.menkalian.vela.featuretoggle.compiler.backend

import de.menkalian.vela.featuretoggle.compiler.backend.kotlin.KotlinSourceGenerator

object Generators {
    val KOTLIN: IGenerator = KotlinSourceGenerator()
}