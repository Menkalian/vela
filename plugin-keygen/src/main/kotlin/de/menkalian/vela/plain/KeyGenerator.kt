package de.menkalian.vela.plain

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import de.menkalian.vela.gradle.KeyObjectExtension
import java.io.File

class KeyGenerator(private val config: KeyObjectExtension) {
    private val loader = TemplateLoader()

    init {
        loader.setReplacement("{{PLACEHOLDER}}" to config.separator)
    }

    fun runGeneration() {
        generateBase()
        generateForFile(File(config.sourceDir))
    }

    fun generateBase() {
        // Generate ParentClass
        ClassWriter("GeneratedKey", config.targetPackage, config)
            .writeText(
                loader.loadTemplate("GeneratedKey.java")
            )
    }

    fun generateForFile(base: File) {
        base.listFiles()?.forEach {
            if (it.isDirectory && config.scanRecursive) {
                generateForFile(it)
            } else {
                val keyTree = YAMLMapper().readTree(transformToYaml(it.readText()))
                val parent =
                    if (config.prefixRecursive && it.parentFile.toURI() != config.sourceDir) {
                        it.parentFile
                            .toRelativeString(File(config.sourceDir))
                            .replace(File.pathSeparator, ".")
                    } else {
                        ""
                    }

                generateObjects(keyTree, null, parent, 0)
            }
        }
    }


    fun generateObjects(valueNode: JsonNode?, classWriter: ClassWriter?, parentKeyPath: String, indentationLevel: Int) {
        if (valueNode == null) return

        val recourse = !valueNode.isTextual && !valueNode.isArray
        val keyNames = when {
            valueNode.isTextual -> listOf(valueNode.textValue())
            valueNode.isArray   -> (valueNode as ArrayNode).map { it.textValue() }
            else                -> valueNode.fieldNames().asSequence().toList()
        }

        keyNames.forEachIndexed { pos, keyName ->
            val cw = classWriter ?: ClassWriter("${keyName}Key", config.targetPackage, config)
            loader.setReplacement("{{PARENT}}" to parentKeyPath.replace(".", config.separator))
            loader.setReplacement("{{KEY_NAME}}" to keyName)

            val toOutput = if (classWriter == null) {
                loader.loadTemplate("TopLevelKeyClass.java")
            } else if (!config.finalLayerAsString || (recourse && !valueNode.get(keyName).isEmpty)) {
                loader.loadTemplate("SubKeyClass.java", indentationLevel)
            } else {
                loader.loadTemplate("StringProperty.java", indentationLevel)
            }

            val outputSplit = toOutput.split("{{CHILDREN}}")

            cw.writeText(outputSplit[0].trimEnd())

            if (outputSplit.size >= 2) {
                if (recourse && !valueNode.get(keyName).isEmpty) {
                    cw.writeText("\n\n")
                    if (parentKeyPath.isBlank())
                        generateObjects(valueNode.get(keyName), cw, keyName, indentationLevel + 1)
                    else
                        generateObjects(valueNode.get(keyName), cw, "$parentKeyPath.$keyName", indentationLevel + 1)
                }

                cw.writeText(outputSplit[1].trimEnd())
            }

            if (pos < keyNames.size - 1)
                cw.writeText("\n\n")
        }
    }
}