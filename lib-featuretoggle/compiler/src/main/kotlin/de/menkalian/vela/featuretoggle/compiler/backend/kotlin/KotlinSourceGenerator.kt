package de.menkalian.vela.featuretoggle.compiler.backend.kotlin

import de.menkalian.vela.featuretoggle.compiler.CompileConfig
import de.menkalian.vela.featuretoggle.compiler.backend.IGenerator
import de.menkalian.vela.featuretoggle.compiler.tree.IFeatureTree
import de.menkalian.vela.featuretoggle.compiler.tree.INode
import de.menkalian.vela.generated.velaKey.vela
import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.parser.ITemplateParser
import java.io.File

internal open class KotlinSourceGenerator : IGenerator {
    lateinit var rootNode: INode.IDependingNode.IFeatureSetNode
    lateinit var rootPackage: String

    override fun generateOutput(tree: IFeatureTree, targetDir: File, targetPackage: String) {
        rootPackage = targetPackage
        generateBaseCode(targetDir, targetPackage)
        generateRootCode(targetDir, targetPackage, tree)
    }

    protected fun generateBaseCode(targetDir: File, targetPackage: String) {
        val rootDir = File(targetDir, targetPackage.replace('.', File.separatorChar))
        if (rootDir.exists().not())
            rootDir.mkdirs()

        val vars = Variables()
        vars[vela.pkg.root] = targetPackage
        vars[vela.pkg.current] = targetPackage

        loadKotlinTemplate("FeatureUtil", rootDir, vars)
        loadKotlinTemplate("IFeature", rootDir, vars)
        loadKotlinTemplate("IFeatureInternal", rootDir, vars)
    }

    protected fun generateRootCode(targetDir: File, targetPackage: String, tree: IFeatureTree) {
        val rootDir = File(targetDir, targetPackage.replace('.', File.separatorChar))
        if (rootDir.exists().not())
            rootDir.mkdirs()

        rootNode = tree.rootNode

        val vars = Variables()
        vars[vela.pkg.root] = targetPackage
        vars[vela.pkg.current] = targetPackage
        vars[vela.immutable] = CompileConfig.mutabilityEnabled.not().toString()
        vars[vela.feature.children.n] = "0"

        vars.addChildren(rootNode.featureNodes.values.toList())
        vars.addChildren(rootNode.subsetNodes.values.toList())

        loadKotlinTemplate("FeatureConfig", rootDir, vars)

        rootNode.subsetNodes.values.forEach { generateNodeCode(targetDir, targetPackage + "." + it.name, it) }
        rootNode.featureNodes.values.forEach { generateNodeCode(targetDir, targetPackage + "." + it.name, it) }
    }

    protected fun generateNodeCode(targetDir: File, targetPackage: String, node: INode.IDependingNode) {
        val rootDir = File(targetDir, targetPackage.replace('.', File.separatorChar))
        if (rootDir.exists().not())
            rootDir.mkdirs()

        val vars = Variables()
        vars[vela.pkg.root] = rootPackage
        vars[vela.pkg.current] = targetPackage
        vars[vela.feature.children.n] = "0"
        vars[vela.feature.implementation.n] = "0"
        vars[vela.feature.dependent.n] = "0"
        vars[vela.feature.dependency.n] = "0"

        vars[vela.feature.name] = node.name
        vars[vela.feature.nameCapitalized] = node.name.capitalize()
        vars[vela.feature.isDefault] = node.isDefaultEnabled.toString()

        if (node is INode.IDependingNode.IFeatureSetNode) {
            vars.addChildren(node.subsetNodes.values.toList())
            vars.addChildren(node.featureNodes.values.toList())
        } else if (node is INode.IDependingNode.IFeatureNode) {
            vars.addImplementations(node.implNodes.values.toList())
        }
        vars.addDependencies(node.dependencyNodes.map { it.target }.filterNotNull().toList())
        vars.addDependents(findDependentNodes(rootNode, node.absoluteName))

        loadKotlinTemplate("BaseFeature", rootDir, vars, "${node.name.capitalize()}Feature")

        if (node is INode.IDependingNode.IFeatureSetNode) {
            node.featureNodes.values.forEach { generateNodeCode(targetDir, targetPackage + "." + it.name, it) }
            node.subsetNodes.values.forEach { generateNodeCode(targetDir, targetPackage + "." + it.name, it) }
        }
    }

    private fun findDependentNodes(rootNode: INode.IDependingNode, requiredNodePath: String): List<INode.IDependingNode> {
        val toReturn = mutableListOf<INode.IDependingNode>()
        when (rootNode) {
            is INode.IDependingNode.IFeatureSetNode -> {
                toReturn.addAll(rootNode.featureNodes.flatMap { findDependentNodes(it.value, requiredNodePath) })
                toReturn.addAll(rootNode.subsetNodes.flatMap { findDependentNodes(it.value, requiredNodePath) })
            }
            is INode.IDependingNode.IFeatureNode    -> {
                toReturn.addAll(rootNode.implNodes.flatMap { findDependentNodes(it.value, requiredNodePath) })
            }
        }
        if (rootNode.dependencyNodes.any { it.name == requiredNodePath })
            toReturn.add(rootNode)

        return toReturn
    }

    private fun loadKotlinTemplate(templateName: String, targetDir: File, vars: Variables, overrideFileName: String = templateName) {
        val outFile = File(targetDir, "$overrideFileName.kt")
        val input = javaClass
            .classLoader
            .getResourceAsStream("vtp/kotlin/$templateName.kt.vtp")

        if (input != null) {
            val output = ITemplateParser
                .getDefaultImplementation()
                .parse(input)
                .evaluate(vars)

            outFile.writeText(output)
        } else if (outFile.exists()) {
            outFile.delete()
        }
    }

    private fun Variables.addChildren(nodes: List<INode.IDependingNode>) {
        addCollection(vela.feature.children.toString(), nodes)
    }

    private fun Variables.addImplementations(nodes: List<INode.IDependingNode.IImplementationNode>) {
        addCollection(vela.feature.implementation.toString(), nodes)

        nodes.forEachIndexed { index, node ->
            val name = vela.feature.implementation.XXX.toString()
                .replace("XXX", String.format("%03d", index + 1))
            addCollection("$name.dependency", node.dependencyNodes.map { it.target }.filterNotNull().toList())
            addCollection("$name.dependent", findDependentNodes(rootNode, node.absoluteName))
        }
    }

    private fun Variables.addDependents(nodes: List<INode.IDependingNode>) {
        addCollection(vela.feature.dependent.toString(), nodes)
    }

    private fun Variables.addDependencies(nodes: List<INode.IDependingNode>) {
        addCollection(vela.feature.dependency.toString(), nodes)
    }

    private fun Variables.addCollection(name: String, nodes: List<INode.IDependingNode>) {
        var currentCount = getOrDefault("$name.n", "0").toInt()
        nodes.forEach {
            currentCount++
            put(String.format("%s.%03d.name", name, currentCount), it.name)
            put(String.format("%s.%03d.nameCapitalized", name, currentCount), it.name.capitalize())
            put(String.format("%s.%03d.path", name, currentCount), it.absoluteName)
            put(String.format("%s.%03d.isDefault", name, currentCount), it.isDefaultEnabled.toString())
        }
        put("$name.n", currentCount.toString())
    }
}