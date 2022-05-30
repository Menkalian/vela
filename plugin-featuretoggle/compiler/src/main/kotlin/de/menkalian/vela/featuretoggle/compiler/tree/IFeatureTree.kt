package de.menkalian.vela.featuretoggle.compiler.tree

interface IFeatureTree {
    val rootNode: INode.IDependingNode.IFeatureSetNode
    val version: Version

    companion object {
        fun create(
            rootNode: INode.IDependingNode.IFeatureSetNode,
            version: Version = Version.latest()
        ): IFeatureTree {
            return FeatureTree(rootNode, version)
        }
    }

    enum class Version {
        VERSION_1;

        companion object {
            fun latest() = VERSION_1
        }
    }

    private data class FeatureTree(override val rootNode: INode.IDependingNode.IFeatureSetNode, override val version: Version) : IFeatureTree
}
