package de.menkalian.vela.featuretoggle.compiler.parser.xml

import de.menkalian.vela.featuretoggle.compiler.tree.DefaultDependencyNode
import de.menkalian.vela.featuretoggle.compiler.tree.DefaultFeatureNode
import de.menkalian.vela.featuretoggle.compiler.tree.DefaultFeatureSetNode
import de.menkalian.vela.featuretoggle.compiler.tree.DefaultImplementationNode
import de.menkalian.vela.featuretoggle.compiler.tree.IFeatureTree
import de.menkalian.vela.featuretoggle.compiler.tree.INode
import org.junit.jupiter.api.Assertions

fun DefaultFeatureSetNode.addSet(name: String, isDefault: Boolean = false): DefaultFeatureSetNode {
    val newSet = DefaultFeatureSetNode(name, this, isDefault)
    subsetNodes[name] = newSet
    return newSet
}

fun DefaultFeatureSetNode.addFeature(name: String, isDefault: Boolean = false): DefaultFeatureNode {
    val newFeat = DefaultFeatureNode(name, this, isDefault)
    featureNodes[name] = newFeat
    return newFeat
}

fun DefaultFeatureNode.addImplementation(name: String, isDefault: Boolean = false): DefaultImplementationNode {
    val newDep = DefaultImplementationNode(name, this, isDefault)
    implNodes[name] = newDep
    return newDep
}

fun DefaultFeatureSetNode.addDependency(target: INode.IDependingNode): DefaultFeatureSetNode {
    val newDep = DefaultDependencyNode(target.absoluteName)
    newDep.parent = this
    newDep.target = target

    return this
}

fun DefaultFeatureNode.addDependency(target: INode.IDependingNode): DefaultFeatureNode {
    val newDep = DefaultDependencyNode(target.absoluteName)
    newDep.parent = this
    newDep.target = target

    return this
}

fun DefaultImplementationNode.addDependency(target: INode.IDependingNode): DefaultImplementationNode {
    val newDep = DefaultDependencyNode(target.absoluteName)
    newDep.parent = this
    newDep.target = target

    return this
}

fun assertTreeEquality(first: IFeatureTree, second: IFeatureTree) {
    Assertions.assertEquals(first.version, second.version)
    assertNodeEquality(first.rootNode, second.rootNode)
}

fun assertNodeEquality(first: INode, second: INode) {
    Assertions.assertEquals(first.name, second.name)
    Assertions.assertEquals(first.absoluteName, second.absoluteName)

    Assertions.assertEquals(first::class, second::class)

    if (first is INode.IDependingNode.IFeatureNode && second is INode.IDependingNode.IFeatureNode) {
        first.implNodes.forEach { assertNodeEquality(first.implNodes[it.key]!!, second.implNodes[it.key]!!) }
        first.dependencyNodes.indices.forEach { assertNodeEquality(first.dependencyNodes[it], second.dependencyNodes[it]) }
    }
    if (first is INode.IDependingNode.IFeatureSetNode && second is INode.IDependingNode.IFeatureSetNode) {
        first.featureNodes.forEach { assertNodeEquality(first.featureNodes[it.key]!!, second.featureNodes[it.key]!!) }
        first.subsetNodes.forEach { assertNodeEquality(first.subsetNodes[it.key]!!, second.subsetNodes[it.key]!!) }
        first.dependencyNodes.indices.forEach { assertNodeEquality(first.dependencyNodes[it], second.dependencyNodes[it]) }
    }
    if (first is INode.IDependingNode.IImplementationNode && second is INode.IDependingNode.IImplementationNode) {
        first.dependencyNodes.indices.forEach { assertNodeEquality(first.dependencyNodes[it], second.dependencyNodes[it]) }
    }
    if (first is INode.IDependencyNode && second is INode.IDependencyNode) {
        assertNodeEquality(first.target!!, second.target!!)
    }
}
