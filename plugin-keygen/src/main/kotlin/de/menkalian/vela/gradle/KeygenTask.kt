package de.menkalian.vela.gradle

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import de.menkalian.vela.template.evaluator.ITemplateEvaluator
import de.menkalian.vela.template.parser.ITemplateParser
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.tasks.TaskAction
import java.io.File

open class KeygenTask : DefaultTask() {
    init {
        group = "generate"
        outputs.upToDateWhen {
            false
        }
    }

    private val generatedBasePackages: MutableList<String> = mutableListOf()

    /**
     * Main Action of the task.
     * It clears the existing files and generates new ones in place of the previous ones.
     */
    @TaskAction
    fun generateKeyObjects() {
        getKeygenExtension()
            .asMap
            .forEach { (name, config) ->
                logger.info("Generating $name: $config")
                KeygenTaskExecution(config).generateSources()
            }
    }


    /**
     * Utility method to obtain the KeygenExtension
     */
    private fun getKeygenExtension(): NamedDomainObjectContainer<KeygenExtension> {
        val container = project.extensions.getByName(KeygenExtension.CONTAINER_NAME)
        if (container is NamedDomainObjectContainer<*>) {
            @Suppress("UNCHECKED_CAST")
            return container as NamedDomainObjectContainer<KeygenExtension>
        }
        throw RuntimeException("Invalid extension: ${KeygenExtension.CONTAINER_NAME} is not a NamedDomainObjectContainer.")
    }

    private inner class KeygenTaskExecution(private val extension: KeygenExtension) {
        /**
         * Get the selected/best generator for the project
         */
        private val generator: KeygenExtension.Generator
            get() {
                val gen = extension.generator.get()
                if (gen == KeygenExtension.Generator.DEFAULT) {
                    return KeygenExtension.Generator.defaultGenerator
                } else {
                    return gen
                }
            }

        private val targetDir: File
            get() = when (generator) {
                KeygenExtension.Generator.JAVA   -> File(extension.targetDir.get().path + "/java")
                KeygenExtension.Generator.KOTLIN -> File(extension.targetDir.get().path + "/kotlin")
                else                             -> throw Exception("Invalid Generator Configuration")
            }

        private val pkgDir = extension.targetPackage.get().replace('.', '/')
        private val keyClassTemplate = loadTemplate("KeyRoot")

        fun generateSources() {
            // Extract KeyLevel.vtp, since it is included in other templates
            extractTemplate("KeyLevel")

            generateBase()
            val srcDir = extension.sourceDir.get()
            generateFilesForSource(srcDir)

            // Delete template file
            File("KeyLevel.vtp").delete()
        }

        private fun generateFilesForSource(src: File) {
            src.listFiles()?.forEach {
                if (it.isDirectory) {
                    if (extension.scanRecursive.get())
                        generateFilesForSource(it)
                } else {
                    val keyTree = YAMLMapper().readTree(transformToYaml(it.readText()))
                    val parent =
                        if (extension.prefixRecursive.get()
                            && it.parentFile != extension.sourceDir.get()
                        ) {
                            it.parentFile
                                .toRelativeString(extension.sourceDir.get())
                                .replace(File.separator, ".")
                        } else {
                            ""
                        }

                    logger.info("Read keys tree: $keyTree")
                    logger.debug("Parent for tree: $parent")
                    val vars = compileSource(keyTree, parent)
                    logger.debug("Template input variables: $vars")
                    val clazz: String
                    try {
                        clazz = keyClassTemplate.evaluate(vars)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        throw RuntimeException(ex)
                    }

                    val clazzFile = File(getClassFile(targetDir.absolutePath + "/" + pkgDir + "/${vars["Vela.Base.Name"]}"))
                    clazzFile.parentFile.mkdirs()
                    clazzFile.writeText(clazz)
                }
            }
        }

        /**
         * Generates basic file
         */
        private fun generateBase() {
            logger.info("Generating basic classes")
            val pkg = extension.targetPackage.get()
            if (generatedBasePackages.contains(pkg))
                return

            generatedBasePackages.add(pkg)

            val baseClass = loadTemplate("BaseKey")
                .evaluate(hashMapOf("Vela.Package" to pkg))

            File(getClassFile(targetDir.absolutePath + "/" + pkgDir + "/BaseKey")).apply {
                parentFile.mkdirs()
                writeText(baseClass)
            }
        }

        private fun compileSource(content: JsonNode, parent: String): HashMap<String, String> {
            val toReturn = hashMapOf<String, String>()
            toReturn["Vela.Package"] = extension.targetPackage.get()
            toReturn["Vela.Separator"] = extension.separator.get()
            toReturn["Vela.Setting.FinalLayerAsString"] = extension.finalLayerAsString.get().toString()

            addKeys(toReturn, parent, content)
            if (parent.isNotBlank()) {
                val parentKeys = parent.trim().split(".")
                var parentStr = ""
                parentKeys.forEach {
                    toReturn["Vela.Keys.$parentStr.n"] = "1"
                    toReturn["Vela.Keys.$parentStr.001.Name"] = it

                    if (parentStr.isNotEmpty()) {
                        parentStr += "."
                    }
                    parentStr += it
                }
            }

            // the double dot is on purpose there, to find the key of the root-level
            toReturn["Vela.Base.Name"] = toReturn["Vela.Keys..001.Name"] ?: ""
            return toReturn
        }

        private fun addKeys(map: MutableMap<String, String>, parent: String, content: JsonNode) {
            if (content.isTextual) {
                map["Vela.Keys.$parent.n"] = "1"
                map["Vela.Keys.$parent.001.Name"] = content.textValue()
                map["Vela.Keys.$parent.${content.textValue()}.n"] = "0"
            } else if (content.isArray && content is ArrayNode) {
                val arrayNode = content.arrayNode()
                map["Vela.Keys.$parent.n"] = arrayNode.count().toString()
                arrayNode.forEachIndexed { idx, node ->
                    map["Vela.Keys.$parent.${(idx + 1).toString().padStart(3, '0')}.Name"] = node.textValue()
                    map["Vela.Keys.$parent.${node.textValue()}.n"] = "0"
                }
            } else {
                map["Vela.Keys.$parent.n"] = content.fields().asSequence().count().toString()
                content.fields().withIndex().forEach {
                    map["Vela.Keys.$parent.${(it.index + 1).toString().padStart(3, '0')}.Name"] = it.value.key
                    val newParent = if (parent.isEmpty()) it.value.key else "$parent.${it.value.key}"
                    addKeys(map, newParent, it.value.value)
                }
            }
        }

        private fun extractTemplate(templateName: String) {
            val stream = this.javaClass.classLoader
                .getResourceAsStream(getTemplatePath(templateName))!!
            File("$templateName.vtp")
                .writeBytes(stream.bufferedReader().readText().toByteArray())
            stream.close()
        }

        /**
         * Loads the template for the current generator
         */
        private fun loadTemplate(templateName: String): ITemplateEvaluator {
            val stream = this.javaClass.classLoader
                .getResourceAsStream(getTemplatePath(templateName))
            return ITemplateParser
                .getDefaultImplementation()
                .parse(stream!!)
                .apply {
                    stream.close()
                }
        }

        /**
         * Appends the correct filename-extension to the name of the generated file
         */
        private fun getClassFile(filename: String): String {
            return when (generator) {
                KeygenExtension.Generator.JAVA   -> "$filename.java"
                KeygenExtension.Generator.KOTLIN -> "$filename.kt"
                else                             -> throw Exception("Invalid Generator Configuration")
            }
        }

        /**
         * Determines which template to use for the current generator
         */
        private fun getTemplatePath(templateName: String): String {
            return when (generator) {
                KeygenExtension.Generator.JAVA   -> "templates/java/$templateName.vtp"
                KeygenExtension.Generator.KOTLIN -> "templates/kotlin/$templateName.vtp"
                else                             -> throw Exception("Invalid Generator Configuration")
            }
        }
    }
}
