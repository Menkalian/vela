package de.menkalian.vela

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerationTask : DefaultTask() {
    private fun getConfig() = project.keygenConfig()

    @OutputDirectory
    fun getTargetDir(): File = File(getConfig().targetDir)

    @InputDirectory
    fun getInputDir(): File = File(getConfig().sourceDir)

    @Input
    fun getSeparator() = getConfig().separator

    @Input
    fun getTargetPackage() = getConfig().targetPackage

    @Input
    fun isScanRecursive() = getConfig().scanRecursive

    @Input
    fun isPrefixRecursive() = getConfig().prefixRecursive

    @Input
    fun isFinalLayerAsString() = getConfig().finalLayerAsString

    @TaskAction
    fun generateKeySources() {
        // Generate ParentClass
        val generationFile = File(getTargetDir(), "${getTargetPackage().replace(".", "/")}/GeneratedKey.java")
        generationFile.parentFile.mkdirs()
        generationFile.writeText(
            loadTemplate(
                "GeneratedKey.java.template",
                mapOf(
                    "{{PACKAGE_NAME}}" to getConfig().targetPackage,
                    "{{PLACEHOLDER}}" to getConfig().separator
                )
            )
        )

        // Search SourceDir
        generateForFile(getInputDir())
    }

    private fun generateForFile(base: File) {
        base.listFiles()?.forEach {
            if (it.isDirectory && getConfig().scanRecursive) {
                generateForFile(it)
            } else {
                val transformed = transformToYaml(it)

                val keyTree = YAMLMapper().readTree(transformed)
                keyTree.fields().forEach { fieldEntry ->
                    val targetFile = File(
                        getTargetDir(),
                        "${getTargetPackage().replace(".", "/")}/${fieldEntry.key}Key.java"
                    )
                    targetFile.createNewFile()

                    val parent = if (getConfig().prefixRecursive && targetFile.parentFile != getInputDir()) {
                        targetFile.parentFile.toRelativeString(getInputDir()).replace(File.pathSeparator, ".")
                    } else {
                        ""
                    }

                    val replacementMap = mapOf(
                        "{{PACKAGE_NAME}}" to getConfig().targetPackage,
                        "{{PLACEHOLDER}}" to getConfig().separator,
                        "{{PARENT}}" to parent,
                        "{{KEY_NAME}}" to fieldEntry.key
                    )

                    val toOutput = loadTemplate("TopLevelKeyClass.java.template", replacementMap)
                    val outputSplit = toOutput.split("{{CHILDREN}}")

                    targetFile.writeText(outputSplit[0])

                    if (outputSplit.size >= 2) {
                        val parentKeyPath = if (parent.isBlank()) {
                            fieldEntry.key
                        } else {
                            "$parent.${fieldEntry.key}"
                        }

                        generateObjects(fieldEntry.value, targetFile, parentKeyPath, 1)

                        targetFile.appendText(outputSplit[1])
                    }
                }
            }
        }
    }


    private fun generateObjects(valueNode: JsonNode?, targetFile: File, parentKeyPath: String, indentationLevel: Int) {
        if (valueNode == null) return

        val recourse = !valueNode.isTextual && !valueNode.isArray
        val keyNames = when {
            valueNode.isTextual -> listOf(valueNode.textValue())
            valueNode.isArray   -> (valueNode as ArrayNode).map { it.textValue() }
            else                -> valueNode.fieldNames().asSequence().toList()
        }

        keyNames.forEachIndexed { pos, keyName ->
            val replacementMap = mapOf(
                "{{PACKAGE_NAME}}" to getConfig().targetPackage,
                "{{PLACEHOLDER}}" to getConfig().separator,
                "{{PARENT}}" to parentKeyPath,
                "{{KEY_NAME}}" to keyName
            )

            val toOutput = if (!getConfig().finalLayerAsString || (recourse && !valueNode.get(keyName).isEmpty)) {
                loadTemplate("SubKeyClass.java.template", replacementMap, indentationLevel)
            } else {
                loadTemplate("StringProperty.java.template", replacementMap, indentationLevel)
            }

            val outputSplit = toOutput.split("{{CHILDREN}}")

            targetFile.appendText(outputSplit[0].trimEnd())

            if (outputSplit.size >= 2) {
                if (recourse && !valueNode.get(keyName).isEmpty) {
                    targetFile.appendText("\n\n")
                    generateObjects(valueNode.get(keyName), targetFile, "$parentKeyPath.$keyName", indentationLevel + 1)
                }

                targetFile.appendText(outputSplit[1].trimEnd())
            }

            if (pos < keyNames.size - 1)
                targetFile.appendText("\n\n")
        }
    }

    private fun loadTemplate(resourceName: String, replacements: Map<String, String> = mapOf(), indentation: Int = 0): String {
        var toReturn = String(javaClass.getResource(resourceName).openStream().readAllBytes())

        replacements.forEach {
            toReturn = toReturn.replace(it.key, it.value)
        }

        toReturn = toReturn.lines().joinToString("\n") { " ".repeat(4 * indentation) + it }

        return toReturn
    }

    private fun transformToYaml(input: File): File {
        val transformed = File.createTempFile("vela", ".yml")
        transformed.deleteOnExit()

        val lines = input.readLines()
        lines.forEach { line ->
            if (!line.contains(':') && line.isNotBlank())
                transformed.appendText("$line:\n")
            else
                transformed.appendText("$line\n")
        }
        return transformed
    }
}