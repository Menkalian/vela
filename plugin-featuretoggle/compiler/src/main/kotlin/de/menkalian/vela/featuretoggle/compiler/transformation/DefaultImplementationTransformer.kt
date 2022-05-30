package de.menkalian.vela.featuretoggle.compiler.transformation

import de.menkalian.vela.featuretoggle.compiler.tree.DefaultImplementationNode
import de.menkalian.vela.featuretoggle.compiler.tree.IFeatureTree
import de.menkalian.vela.featuretoggle.compiler.tree.INode

/**
 * Modifies the Implementations of all Features so the following conditions are true:
 *
 *  - All Features have at least one implementation. In none is present there will be a node generated called "DEFAULT" </li>
 *  - There is always exactly one implementation marked with the "isDefault"-Flag.
 *    If there are no such implementations given, the first one (lexical order of the name) is marked default.
 *    If there are multiple implementations marked, the first one (lexical order of the name), among the already marked ones, is marked default.
 */
class DefaultImplementationTransformer : ITreeTransformer {
    override fun getPriority() = 1024

    override fun apply(tree: IFeatureTree): Boolean {
        traverseFeatureSet(tree.rootNode)
        return true
    }

    private fun traverseFeatureSet(set: INode.IDependingNode.IFeatureSetNode) {
        set.subsetNodes.forEach { traverseFeatureSet(it.value) }
        set.featureNodes.forEach { ensureFeatureImplementations(it.value) }
    }

    private fun ensureFeatureImplementations(value: INode.IDependingNode.IFeatureNode) {
        if (value.implNodes.isEmpty()) {
            value.implNodes["DEFAULT"] = DefaultImplementationNode("DEFAULT", value, true)
        } else {
            val amountDefault = value.implNodes.filter { it.value.isDefaultEnabled }.count()
            if (amountDefault < 1) {
                value.implNodes
                    .minByOrNull { it.key }!!
                    .value.isDefaultEnabled = true
            } else if (amountDefault > 1) {
                value.implNodes.entries
                    .filter { it.value.isDefaultEnabled }
                    .sortedBy { it.key }
                    .map {
                        // Disable default for all
                        it.value.isDefaultEnabled = false
                        it
                    }
                    // Reenable default for the first one that was previously enabled
                    .first().value.isDefaultEnabled = true
            }
        }
    }

    companion object : ITreeTransformer.ITreeTranformerFactory {
        override fun create(): ITreeTransformer = DefaultImplementationTransformer()
    }
}