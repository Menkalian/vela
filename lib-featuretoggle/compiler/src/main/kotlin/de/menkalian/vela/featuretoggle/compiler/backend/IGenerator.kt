package de.menkalian.vela.featuretoggle.compiler.backend

import de.menkalian.vela.featuretoggle.compiler.tree.IFeatureTree
import java.io.File

fun interface IGenerator {
    fun generateOutput(tree: IFeatureTree, targetDir: File)
}