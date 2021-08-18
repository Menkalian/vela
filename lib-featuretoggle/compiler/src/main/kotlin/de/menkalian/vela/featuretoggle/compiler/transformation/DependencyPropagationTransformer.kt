package de.menkalian.vela.featuretoggle.compiler.transformation

import de.menkalian.vela.featuretoggle.compiler.tree.DefaultDependencyNode
import de.menkalian.vela.featuretoggle.compiler.tree.IFeatureTree
import de.menkalian.vela.featuretoggle.compiler.tree.INode

/**
 * Propagates the dependencies from parents to their child nodes.
 *
 * To make this possible the following assumptions/definitions were made:
 *  - all child-nodes depend on their parent-nodes
 *  - all child-nodes depend on their parent-nodes' dependencies
 *  - after this transformation, every node depends on **exactly** the dependency nodes that are in it's dependencies property
 */
class DependencyPropagationTransformer : ITreeTransformer {
    override fun getPriority() = 2048

    override fun apply(tree: IFeatureTree): Boolean {
        tree.rootNode.subsetNodes.forEach { processNode(it.value, listOf()) }
        tree.rootNode.featureNodes.forEach { processNode(it.value, listOf()) }

        return true
    }

    private fun processNode(node: INode.IDependingNode, propagetedDependencies: List<INode.IDependencyNode>) {
        val clonedDependencies = propagetedDependencies
            // Filter dependencies already present
            .filter { node.dependencyNodes.none { present -> present.name == it.name } }
            // Create the new dependency nodes
            .map {
                val cloneNode = DefaultDependencyNode(it.name)
                cloneNode.target = it.target
                cloneNode.parent = node
                cloneNode
            }

        node.dependencyNodes.addAll(clonedDependencies)

        val selfDependency = DefaultDependencyNode(node.absoluteName)
        selfDependency.parent = null
        selfDependency.target = node
        val toPropagate: MutableList<INode.IDependencyNode> = mutableListOf(selfDependency)
        toPropagate.addAll(node.dependencyNodes)

        when (node) {
            is INode.IDependingNode.IFeatureSetNode -> {
                node.subsetNodes.forEach { processNode(it.value, toPropagate) }
                node.featureNodes.forEach { processNode(it.value, toPropagate) }
            }
            is INode.IDependingNode.IFeatureNode    -> {
                node.implNodes.forEach { processNode(it.value, toPropagate) }
            }
        }
    }

    companion object : ITreeTransformer.ITreeTranformerFactory {
        override fun create(): ITreeTransformer = DependencyPropagationTransformer()
    }
}