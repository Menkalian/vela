package de.menkalian.vela

import com.android.build.api.dsl.CommonExtension
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import java.io.File

const val EXTENSION_NAME = "de.menkalian.vela.keyobjectgen"

class KeyObjectGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create(EXTENSION_NAME, KeyObjectExtension::class.java, target)
        val generationTask = target.tasks.create("generateKeyObjects", GenerationTask::class.java)

        target.pluginManager.withPlugin("java") {
            target.sourceSets().getByName("main").java.srcDir(target.keyobjectgen().targetDir)
        }
        target.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            target.sourceSets().getByName("main").java.srcDir(target.keyobjectgen().targetDir)
        }

        target.afterEvaluate {
            target.pluginManager.withPlugin("java") {
                target.tasks.getByName("compileJava").dependsOn(generationTask)
            }
            target.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
                target.tasks.getByName("compileKotlin").dependsOn(generationTask)
            }
            target.pluginManager.withPlugin("com.android.base") {
                target.buildTypeNames().forEach {
                    target.tasks.getByName("compile${it.capitalize()}Java").dependsOn.add(generationTask)
                }
                val generationBaseDir = target.keyobjectgen().targetDir
                generationBaseDir.mkdirs()
                (target.extensions.getByName("android") as? CommonExtension<*, *, *, *, *, *, *, *>)!!.sourceSets.getByName("main").java.srcDir(
                    generationBaseDir
                )
            }
        }
    }
}

internal fun Project.buildTypeNames(): Set<String> =
    ((extensions.getByName("android") as? CommonExtension<*, *, *, *, *, *, *, *>)!!.buildTypes.names)

internal fun Project.sourceSets(): NamedDomainObjectContainer<SourceSet> =
    extensions.getByName("sourceSets") as SourceSetContainer

internal fun Project.keyobjectgen(): KeyObjectExtension {
    return extensions.getByName(EXTENSION_NAME) as KeyObjectExtension
}

open class GenerationTask : DefaultTask() {
    @OutputDirectory
    fun getTargetDir(): File = project.keyobjectgen().targetDir

    @InputDirectory
    fun getInputDir(): File = project.keyobjectgen().baseDir

    @TaskAction
    fun generateObjects() {
        generateForFile(project.keyobjectgen().baseDir)
    }

    private fun generateForFile(base: File) {
        base.listFiles()?.forEach {
            val generationFile = File(project.keyobjectgen().targetDir, "de/menkalian/vela/generated/GeneratedKey.java")
            generationFile.parentFile.mkdirs()
            generationFile.writeText(
                """
                    package de.menkalian.vela.generated;
                    
                    public class GeneratedKey {
                        protected final String BASE;
                    
                        GeneratedKey(String key, String parent) {
                            if(parent == null){
                                BASE = key;
                            } else {
                                BASE = parent + "." + key;
                            }
                        }
                        
                        @Override
                        public String toString() {
                            return BASE;
                        }
                    }
                """.trimIndent()
            )

            if (it.isDirectory && project.keyobjectgen().scanRecursive) {
                generateForFile(it)
            } else {
                val keyTree = YAMLMapper().readTree(it)
                keyTree.fields().forEach {
                    val keyGenerationFile = File(project.keyobjectgen().targetDir, "de/menkalian/vela/generated/${it.key}Key.java")
                    keyGenerationFile.createNewFile()

                    keyGenerationFile.writeText(
                        """
                        package de.menkalian.vela.generated;
                    
                        public class ${it.key}Key extends GeneratedKey {
                            public static ${it.key}Key ${it.key} = new ${it.key}Key();
                        
                            private ${it.key}Key () {
                                super("${it.key}", null);
                            }

                    """.trimIndent()
                    )

                    addObjectsToFile(it.value, keyGenerationFile, it.key)

                    keyGenerationFile.appendText("}")
                }
            }
        }
    }

    private fun addObjectsToFile(value: JsonNode?, keyGenerationFile: File, parent: String) {
        if (value == null) return

        if (value.isTextual) {
            keyGenerationFile.appendText(
                """
                    public ${value}Key $value = new ${value}Key();
                    public static class ${value}Key extends GeneratedKey {
                   
                        private ${value}Key () {
                            super("$value", ${parent}.toString());
                        }
                    }
                    """.trimIndent()
            )
            return
        }

        if (value.isArray) {
            (value as ArrayNode).forEach {
                keyGenerationFile.appendText(
                    """
                    public ${it.textValue()}Key ${it.textValue()} = new ${it.textValue()}Key();
                    public static class ${it.textValue()}Key extends GeneratedKey {
                   
                        private ${it.textValue()}Key () {
                            super("${it.textValue()}", ${parent}.toString());
                        }
                    }
                    """.trimIndent()
                )
            }
            return
        }

        value.fields().forEach {
            keyGenerationFile.appendText(
                """
                public ${it.key}Key ${it.key} = new ${it.key}Key();
                public static class ${it.key}Key extends GeneratedKey {
                
                    private ${it.key}Key () {
                        super("${it.key}", ${parent}.toString());
                    }
                """.trimIndent()
            )

            addObjectsToFile(it.value, keyGenerationFile, "$parent.${it.key}")

            keyGenerationFile.appendText("}")
        }
    }
}