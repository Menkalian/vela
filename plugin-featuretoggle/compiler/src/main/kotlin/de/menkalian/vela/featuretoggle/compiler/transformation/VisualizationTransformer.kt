package de.menkalian.vela.featuretoggle.compiler.transformation

import de.menkalian.vela.featuretoggle.compiler.tree.IFeatureTree
import de.menkalian.vela.featuretoggle.compiler.tree.INode
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Does not manipulate the Tree, it only visualizes the current state of the tree.
 * Therefore two versions are present: before and after.
 * The only difference between those two is, their priority
 */
class VisualizationTransformer(private val prio: Int) : ITreeTransformer {
    override fun getPriority() = prio

    override fun apply(tree: IFeatureTree): Boolean {
        // Print the header
        println(
            """
                +------------------------------------------------------------------------------+
                |  ABSTRACT SYNTAX TREE                                                        |
                |  Vela Featuretoggle Compiler                                                 |
                |                                                                              |
                |  Tree data version: ${tree.version.name.padEnd(57, ' ')}|
                |                                                                              |
                |  Timestamp: ${OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME).padEnd(65, ' ')}|
                |  Visualization generated by vela                                             |
                +------------------------------------------------------------------------------+
            """.trimIndent()
        )
        printYamlLike(tree.rootNode, -1)

        println()
        println("-".repeat(80))
        println()
        printDependencies(tree.rootNode)
        println()

        return true
    }

    private fun printYamlLike(node: INode, indent: Int) {
        // Build the format template
        val format =
            if (indent >= 0)
                "* %s%s%s%s".format(
                    " ".repeat(indent * 2),
                    "%s: ",
                    if (node is INode.IDependingNode) {
                        if (node.isDefaultEnabled) "(x) " else "( ) "
                    } else {
                        ""
                    },
                    node.name
                )
            else
                "*"

        when (node) {
            is INode.IDependingNode.IFeatureSetNode     -> {
                println(format.format("S"))
                node.subsetNodes.forEach { printYamlLike(it.value, indent + 1) }
                node.featureNodes.forEach { printYamlLike(it.value, indent + 1) }
            }
            is INode.IDependingNode.IFeatureNode        -> {
                println(format.format("F"))
                node.implNodes.forEach { printYamlLike(it.value, indent + 1) }
            }
            is INode.IDependingNode.IImplementationNode -> {
                println(format.format("I"))
            }
            else                                        -> {
                println(format.format("X"))
            }
        }
    }

    private fun printDependencies(node: INode.IDependingNode) {
        if (node.dependencyNodes.isNotEmpty()) {
            println()
            println("Dependencies for: ${node.absoluteName}:")
            node.dependencyNodes.forEach { println("  -> ${it.name}") }
            println()
        }

        when (node) {
            is INode.IDependingNode.IFeatureSetNode -> {
                node.subsetNodes.forEach { printDependencies(it.value) }
                node.featureNodes.forEach { printDependencies(it.value) }
            }
            is INode.IDependingNode.IFeatureNode    -> {
                node.implNodes.forEach { printDependencies(it.value) }
            }
        }
    }

    class Before {
        companion object : ITreeTransformer.ITreeTranformerFactory {
            override fun create() = VisualizationTransformer(Int.MIN_VALUE + 1)
        }
    }

    class After {
        companion object : ITreeTransformer.ITreeTranformerFactory {
            override fun create() = VisualizationTransformer(Int.MAX_VALUE - 1)
        }
    }
}