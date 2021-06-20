package de.menkalian.vela.featuretoggle.compiler.backend.kotlin

import de.menkalian.vela.featuretoggle.compiler.CompileConfig
import de.menkalian.vela.featuretoggle.compiler.backend.IGenerator
import de.menkalian.vela.featuretoggle.compiler.tree.IFeatureTree
import de.menkalian.vela.featuretoggle.compiler.tree.INode
import de.menkalian.vela.featuretoggle.compiler.util.TemplateUtil
import de.menkalian.vela.generated.VelaKey.Vela
import java.io.File

internal open class KotlinSourceGenerator : IGenerator {
    lateinit var rootNode: INode.IDependingNode.IFeatureSetNode
    lateinit var rootPackage: String

    override fun generateOutput(tree: IFeatureTree, targetDir: File, targetPackage: String) {
        rootPackage = targetPackage
        generateBaseCode(targetDir, targetPackage)
        generateRootCode(targetDir, targetPackage, tree)

        rootNode.subsetNodes.values.forEach { generateNodeCode(targetDir, targetPackage, it) }
        rootNode.featureNodes.values.forEach { generateNodeCode(targetDir, targetPackage, it) }
    }

    protected fun generateBaseCode(srcDir: File, pckg: String) {
        val root = File(srcDir, pckg.replace('.', File.separatorChar))
        if (root.exists())
            root.deleteRecursively()
        root.mkdirs()

        val util = TemplateUtil()
        util.setReplacement(Vela.Base.Package to pckg)
        util.setReplacement(Vela.Base.Root.Package to rootPackage)
        util.setReplacement(Vela.Feature.Mutability.Enabled to CompileConfig.mutabilityEnabled.toString())

        util.generateFile(root, FeatureUtil)
        util.generateFile(root, IBuilder)
        util.generateFile(root, IDependable)
        util.generateFile(root, IEnableable)
        util.generateFile(root, IFeature)
        util.generateFile(root, IFeatureRoot)
    }

    protected fun generateRootCode(srcDir: File, pckg: String, tree: IFeatureTree) {
        val rootDir = File(srcDir, pckg.replace('.', File.separatorChar))
        if (rootDir.exists().not())
            rootDir.mkdirs()
        rootNode = tree.rootNode

        val util = TemplateUtil()
        util.setReplacement(Vela.Base.Package to pckg)
        util.setReplacement(Vela.Base.Root.Package to rootPackage)
        util.setReplacementsFor(rootNode, ALL)
        util.setReplacement(Vela.Feature.Mutability.Enabled to CompileConfig.mutabilityEnabled.toString())

        util.generateFile(rootDir, Features, ALL)
        util.generateFile(rootDir, FeatureConfig, ALL)
    }

    protected fun generateNodeCode(srcDir: File, pckg: String, node: INode.IDependingNode) {
        val rootDir = File(srcDir, pckg.replace('.', File.separatorChar))
        if (rootDir.exists().not())
            rootDir.mkdirs()

        val util = TemplateUtil()
        util.setReplacement(Vela.Base.Package to pckg)
        util.setReplacement(Vela.Base.Root.Package to rootPackage)
        util.setReplacement(Vela.Feature.Mutability.Enabled to CompileConfig.mutabilityEnabled.toString())
        util.setReplacementsFor(node, FEAT_FIELD)

        val builderClassFile = File(rootDir, "${node.name}FeatureBuilder.kt")
        builderClassFile.writeText(util.loadPath(FeatureBuilderClass, FEATURE, 0))
        val classFile = File(rootDir, "${node.name}Feature.kt")
        classFile.writeText(util.loadPath(FeatureClass, FEATURE, 0))

        if (node is INode.IDependingNode.IFeatureSetNode) {
            node.featureNodes.values.forEach { generateNodeCode(srcDir, pckg + "." + node.name, it) }
            node.subsetNodes.values.forEach { generateNodeCode(srcDir, pckg + "." + node.name, it) }
        }
    }

    private fun loadFieldTemplate(node: INode.IDependingNode, filename: String, folder: String = BASE): String {
        val templUtil = TemplateUtil()
        templUtil.setReplacement(Vela.Feature.Name to node.name)
        templUtil.setReplacement(Vela.Feature.Path to node.absoluteName)
        return templUtil.loadPath(filename, folder, 1)
    }

    private fun findDependentNodes(node: INode.IDependingNode, absoluteName: String): List<INode.IDependingNode> {
        val toReturn = mutableListOf<INode.IDependingNode>()
        when (node) {
            is INode.IDependingNode.IFeatureSetNode -> {
                toReturn.addAll(node.featureNodes.flatMap { findDependentNodes(it.value, absoluteName) })
                toReturn.addAll(node.subsetNodes.flatMap { findDependentNodes(it.value, absoluteName) })
            }
            is INode.IDependingNode.IFeatureNode    -> {
                toReturn.addAll(node.implNodes.flatMap { findDependentNodes(it.value, absoluteName) })
            }
        }
        if (node.dependencyNodes.any { it.name == absoluteName })
            toReturn.add(node)

        return toReturn
    }

    //region Template Util Extensions
    private fun TemplateUtil.generateFile(root: File, filename: String, folder: String = BASE) {
        val fileContent = loadTemplate("kotlin/$folder/$filename")
        val file = File(root, filename)
        file.writeText(fileContent)
    }

    private fun TemplateUtil.loadPath(filename: String, folder: String = BASE, indent: Int = 1): String = loadTemplate("kotlin/$folder/$filename", indent)

    private fun TemplateUtil.setReplacementsFor(node: INode.IDependingNode, codePath: String = FEAT_FIELD) {
        setReplacement(Vela.Feature.Name to node.name)
        setReplacement(Vela.Feature.Path to node.absoluteName)
        setReplacement(Vela.Feature.Enabled to node.isDefaultEnabled.toString())

        if (node.parent == null || node.parent == rootNode) {
            setReplacement(Vela.Feature.Parent.Name to "")
            setReplacement(Vela.Feature.Parent.Path to "")

            setReplacement(Vela.Feature.Parent.Field.Instance to loadPath(ParentNullFieldTemplate, FEAT_FIELD, 1))
            setReplacement(Vela.Feature.Parent.Field.Builder to loadPath(ParentNullFieldTemplate, FEAT_FIELD, 1))
        } else {
            setReplacement(Vela.Feature.Parent.Name to (node.parent?.name ?: ""))
            setReplacement(Vela.Feature.Parent.Path to (node.parent?.absoluteName ?: ""))

            setReplacement(Vela.Feature.Parent.Field.Instance to loadPath(ParentInstanceFieldTemplate, FEAT_FIELD, 1))
            setReplacement(Vela.Feature.Parent.Field.Builder to loadPath(ParentBuilderFieldTemplate, FEAT_FIELD, 1))
        }

        setReplacement(Vela.Feature.Dependencies.List to node.dependencyNodes.joinToString(",\n") { "        root.${it.name}" })

        val dependentNodes = findDependentNodes(rootNode, node.absoluteName)
        setReplacement(Vela.Feature.Dependencies.Dependents.List to dependentNodes.joinToString(",\n") { "        root.${it.absoluteName}" })

        when (node) {
            is INode.IDependingNode.IFeatureSetNode     -> implSetReplacementsFor(node, codePath)
            is INode.IDependingNode.IFeatureNode        -> implSetReplacementsFor(node, codePath)
            is INode.IDependingNode.IImplementationNode -> implSetReplacementsFor(node, codePath)
        }
    }

    private fun TemplateUtil.implSetReplacementsFor(set: INode.IDependingNode.IFeatureSetNode, codePath: String = FEAT_FIELD) {
        // set implementations code blank
        setReplacement(Vela.Feature.Code.Optional.Builder.Implementation to "")
        setReplacement(Vela.Feature.Code.Optional.Disable.Implementation to "")
        setReplacement(Vela.Feature.Code.Optional.Enable.Implementation to "")
        setReplacement(Vela.Feature.Code.Optional.Reset.Implementation to "")
        setReplacement(Vela.Feature.Code.Optional.Implementation to "")

        // Set values for children
        setReplacement(Vela.Feature.Children.Sets.List to set.subsetNodes.values.joinToString(",\n") { it.name })
        setReplacement(Vela.Feature.Children.Features.List to set.featureNodes.values.joinToString(",\n") { it.name })

        setReplacement(Vela.Feature.Children.Implementations.Default to "")
        setReplacement(Vela.Feature.Children.Implementations.List to "")

        val subnodes = mutableListOf<INode.IDependingNode>()
        subnodes.addAll(set.subsetNodes.values)
        subnodes.addAll(set.featureNodes.values)
        setReplacement(Vela.Feature.Children.All.Code.Fields.Instance to subnodes.joinToString("\n") { loadFieldTemplate(it, InstanceFieldTemplate, codePath) })
        setReplacement(Vela.Feature.Children.All.Code.Fields.Builder to subnodes.joinToString("\n") { loadFieldTemplate(it, BuilderFieldTemplate, codePath) })
    }

    private fun TemplateUtil.implSetReplacementsFor(feat: INode.IDependingNode.IFeatureNode, codePath: String = FEAT_FIELD) {
        // set implementations code blank
        setReplacement(Vela.Feature.Code.Optional.Implementation to "")
        setReplacement(Vela.Feature.Code.Optional.Builder.Implementation to "")
        setReplacement(Vela.Feature.Code.Optional.Disable.Implementation to "")
        setReplacement(Vela.Feature.Code.Optional.Enable.Implementation to "")
        setReplacement(Vela.Feature.Code.Optional.Reset.Implementation to "")

        // Set values for children
        setReplacement(Vela.Feature.Children.Sets.List to "")
        setReplacement(Vela.Feature.Children.Features.List to "")

        setReplacement(Vela.Feature.Children.Implementations.Default to feat.implNodes.values.first { it.isDefaultEnabled }.name)
        setReplacement(Vela.Feature.Children.Implementations.List to feat.implNodes.values.joinToString(",\n") { it.name })

        setReplacement(Vela.Feature.Children.All.Code.Fields.Instance to feat.implNodes.values.joinToString("\n") {
            loadFieldTemplate(
                it,
                InstanceFieldTemplate,
                codePath
            )
        })
        setReplacement(Vela.Feature.Children.All.Code.Fields.Builder to feat.implNodes.values.joinToString("\n") {
            loadFieldTemplate(
                it,
                BuilderFieldTemplate,
                codePath
            )
        })
    }

    private fun TemplateUtil.implSetReplacementsFor(impl: INode.IDependingNode.IImplementationNode, codePath: String = FEAT_FIELD) {
        // set implementations code blank
        setReplacement(Vela.Feature.Code.Optional.Builder.Implementation to "")
        setReplacement(Vela.Feature.Code.Optional.Disable.Implementation to "")
        setReplacement(Vela.Feature.Code.Optional.Enable.Implementation to "")
        setReplacement(Vela.Feature.Code.Optional.Reset.Implementation to "")
        setReplacement(Vela.Feature.Code.Optional.Implementation to "")

        // Set values for children
        setReplacement(Vela.Feature.Children.Sets.List to "")
        setReplacement(Vela.Feature.Children.Sets.List to "")

        setReplacement(Vela.Feature.Children.Implementations.Default to "")
        setReplacement(Vela.Feature.Children.Implementations.List to "")

        setReplacement(Vela.Feature.Children.All.Code.Fields.Instance to "")
        setReplacement(Vela.Feature.Children.All.Code.Fields.Builder to "")
    }
    //endregion Template Util Extensions
}