package de.menkalian.vela.featuretoggle.compiler.tree

/**
 * Interface Definitions
 */
interface INode {
    val name: String
    val parent: INode?

    val absoluteName: String
        get() {
            val par = parent
            val absName = par?.absoluteName ?: ""
            return if (absName.isBlank()) {
                name
            } else {
                "$absName.$name"
            }
        }

    interface IDependingNode : INode {
        var isDefaultEnabled: Boolean
        val dependencyNodes: MutableList<IDependencyNode>

        interface IFeatureSetNode : IDependingNode {
            override val parent: IFeatureSetNode?
            val subsetNodes: MutableMap<String, IFeatureSetNode>
            val featureNodes: MutableMap<String, IFeatureNode>
        }

        interface IFeatureNode : IDependingNode {
            override val parent: IFeatureSetNode
            val implNodes: MutableMap<String, IImplementationNode>
        }

        interface IImplementationNode : IDependingNode {
            override val parent: IFeatureNode
        }
    }

    interface IDependencyNode : INode {
        override var parent: IDependingNode?
        var target: IDependingNode?
    }
}
