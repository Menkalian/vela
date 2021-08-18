package de.menkalian.vela.featuretoggle.compiler.tree

data class DefaultDependencyNode(override val name: String) : INode.IDependencyNode {
    override var parent: INode.IDependingNode? = null
    override var target: INode.IDependingNode? = null
}

data class DefaultFeatureSetNode(
    override val name: String,
    override val parent: INode.IDependingNode.IFeatureSetNode?,
    override var isDefaultEnabled: Boolean = false
) : INode.IDependingNode.IFeatureSetNode {
    override val subsetNodes: MutableMap<String, INode.IDependingNode.IFeatureSetNode> = hashMapOf()
    override val featureNodes: MutableMap<String, INode.IDependingNode.IFeatureNode> = hashMapOf()
    override val dependencyNodes: MutableList<INode.IDependencyNode> = mutableListOf()
}

data class DefaultFeatureNode(
    override val name: String,
    override val parent: INode.IDependingNode.IFeatureSetNode,
    override var isDefaultEnabled: Boolean = false,
) : INode.IDependingNode.IFeatureNode {
    override val implNodes: MutableMap<String, INode.IDependingNode.IImplementationNode> = hashMapOf()
    override val dependencyNodes: MutableList<INode.IDependencyNode> = mutableListOf()
}

data class DefaultImplementationNode(
    override val name: String,
    override val parent: INode.IDependingNode.IFeatureNode,
    override var isDefaultEnabled: Boolean = false
) : INode.IDependingNode.IImplementationNode {
    override val dependencyNodes: MutableList<INode.IDependencyNode> = mutableListOf()
}