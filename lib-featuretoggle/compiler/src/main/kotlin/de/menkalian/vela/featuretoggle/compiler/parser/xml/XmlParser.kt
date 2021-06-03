package de.menkalian.vela.featuretoggle.compiler.parser.xml

import de.menkalian.vela.featuretoggle.compiler.parser.DependencyResolutionFailedException
import de.menkalian.vela.featuretoggle.compiler.parser.IParser
import de.menkalian.vela.featuretoggle.compiler.parser.SyntaxInvalidException
import de.menkalian.vela.featuretoggle.compiler.tree.DefaultDependencyNode
import de.menkalian.vela.featuretoggle.compiler.tree.DefaultFeatureNode
import de.menkalian.vela.featuretoggle.compiler.tree.DefaultFeatureSetNode
import de.menkalian.vela.featuretoggle.compiler.tree.DefaultImplementationNode
import de.menkalian.vela.featuretoggle.compiler.tree.IFeatureTree
import de.menkalian.vela.featuretoggle.compiler.tree.INode
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

class XmlParser private constructor(val document: Document) : IParser {
    private val dependencyResolutionActions: MutableList<IDependencyResolutionAction> = mutableListOf()

    override fun parse(): IFeatureTree {
        // Get the root-node from Document
        val rootNodeList = document.getElementsByTagNameNS(XML_NAMESPACE_URI, XML_ROOT_ELEMENT_NAME)
        if (rootNodeList.length < 0) {
            throw SyntaxInvalidException("No features element found in XML root")
        }
        if (rootNodeList.length > 1) {
            throw SyntaxInvalidException("More than one features element found in XML root. Only one is supported!")
        }

        // Parse to feature tree
        val builtTree = IFeatureTree.create(
            parseRootNode(rootNodeList.item(0)),
            IFeatureTree.Version.VERSION_1
        )

        // resolve dependencies
        val success = AtomicBoolean(true)
        dependencyResolutionActions.forEach {
            try {
                it.onTreeBuilt(builtTree)
            } catch (ex: DependencyResolutionFailedException) {
                ex.printStackTrace()
                success.set(false)
            }
        }
        if (!success.get()) {
            throw RuntimeException("There were unresolved dependencies. Aborting compilation!")
        }

        // compilation done
        return builtTree
    }

    private fun parseRootNode(node: Node): INode.IDependingNode.IFeatureSetNode {
        val rootNode = DefaultFeatureSetNode("", null)

        node.childNodes.forEach {
            if (isFeatureSet(it))
                parseFeatureSetNode(it, rootNode)
            else if (isFeature(it))
                parseFeatureNode(it, rootNode)
        }

        return rootNode
    }

    private fun parseFeatureSetNode(node: Node, parent: INode.IDependingNode.IFeatureSetNode) {
        val nameSplit = getName(node).split(".")
        val isDefault = getIsDefault(node)
        val createdNodes = mutableListOf<DefaultFeatureSetNode>()

        nameSplit.forEachIndexed { i, name ->
            if (i == 0) {
                createdNodes.add(DefaultFeatureSetNode(name, parent, isDefault))
                parent.subsetNodes[name] = createdNodes.first()
            } else {
                val element = DefaultFeatureSetNode(name, createdNodes.last(), isDefault)
                createdNodes.last().subsetNodes[name] = element
                createdNodes.add(element)
            }
        }

        node.childNodes.forEach {
            if (isDependency(it))
                parseDependencyNode(it, createdNodes.last())
            if (isFeatureSet(it))
                parseFeatureSetNode(it, createdNodes.last())
            if (isFeature(it))
                parseFeatureNode(it, createdNodes.last())
        }
    }

    private fun parseFeatureNode(node: Node, parent: INode.IDependingNode.IFeatureSetNode) {
        val featureNode = DefaultFeatureNode(
            getName(node), parent, getIsDefault(node)
        )
        parent.featureNodes[featureNode.name] = featureNode

        node.childNodes.forEach {
            if (isDependency(it))
                parseDependencyNode(it, featureNode)
            if (isImplementation(it))
                parseImplementationNode(it, featureNode)
        }
    }

    private fun parseImplementationNode(node: Node, parent: INode.IDependingNode.IFeatureNode) {
        val implNode = DefaultImplementationNode(
            getName(node), parent, getIsDefault(node)
        )
        parent.implNodes[implNode.name] = implNode

        node.childNodes.forEach {
            if (isDependency(it))
                parseDependencyNode(it, implNode)
        }
    }

    private fun parseDependencyNode(node: Node, parent: INode.IDependingNode) {
        val dependencyName = node.textContent
        val depNode = DefaultDependencyNode(dependencyName)

        depNode.parent = parent
        parent.dependencyNodes.add(depNode)

        // Add dependency resolution action
        dependencyResolutionActions.add(object : IDependencyResolutionAction {
            override fun onTreeBuilt(tree: IFeatureTree) {
                val dependency = findComponent(dependencyName, tree)
                    ?: throw DependencyResolutionFailedException(depNode, parent)
                depNode.target = dependency
            }
        })
    }

    private fun isFeatureSet(node: Node): Boolean = node.localName == XML_FEATURE_SET_ELEMENT_NAME
    private fun isFeature(node: Node): Boolean = node.localName == XML_FEATURE_ELEMENT_NAME
    private fun isImplementation(node: Node): Boolean = node.localName == XML_IMPLEMENTATION_ELEMENT_NAME
    private fun isDependency(node: Node): Boolean = node.localName == XML_DEPENDENCY_ELEMENT_NAME

    private fun getIsDefault(node: Node): Boolean =
        node.attributes?.getNamedItemNS(XML_NAMESPACE_URI, XML_IS_DEFAULT_ATTR_NAME)?.nodeValue?.toBoolean() ?: isFeatureSet(node)

    private fun getName(node: Node): String = node.attributes?.getNamedItemNS(XML_NAMESPACE_URI, XML_NAME_ATTR_NAME)?.nodeValue ?: ""

    companion object : IParser.IParserCompanion {
        override val supportedFileExtensions = listOf("xml")

        override fun createInstance(inputStream: InputStream): IParser {
            try {
                val xmlBytes = inputStream.readAllBytes()
                inputStream.close()

                // Validate document first
                val schema = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(
                        XmlParser::class.java.classLoader.getResource("schema/features_schema.xsd")
                    )
                schema.newValidator().validate(StreamSource(xmlBytes.inputStream()))

                val documentFactory = DocumentBuilderFactory.newInstance()
                documentFactory.isNamespaceAware = true
                documentFactory.isXIncludeAware = true

                val documentBuilder = documentFactory.newDocumentBuilder()
                val document = documentBuilder.parse(xmlBytes.inputStream())

                return XmlParser(document)
            } catch (ex: Exception) {
                throw SyntaxInvalidException("Could not parse/validate feature XML-data", ex)
            }
        }
    }

    fun interface IDependencyResolutionAction {
        fun onTreeBuilt(tree: IFeatureTree)

        fun findComponent(name: String, tree: IFeatureTree): INode.IDependingNode? {
            return findInMap(name, tree.rootNode.subsetNodes) ?: findInMap(name, tree.rootNode.featureNodes)
        }

        fun findInMap(name: String, map: Map<String, INode.IDependingNode>): INode.IDependingNode? {
            val split = name.split(".", limit = 2)
            return if (split.size > 1) {
                when (val found = map[split[0]]) {
                    is INode.IDependingNode.IFeatureNode    -> findInMap(split[1], found.implNodes)
                    is INode.IDependingNode.IFeatureSetNode -> (findInMap(split[1], found.subsetNodes) ?: findInMap(split[1], found.featureNodes))
                    else                                    -> null
                }
            } else {
                map[split[0]]
            }
        }
    }
}
