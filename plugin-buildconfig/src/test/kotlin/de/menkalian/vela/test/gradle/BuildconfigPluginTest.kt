package de.menkalian.vela.test.gradle

import de.menkalian.vela.epc.Epc
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.io.TempDir
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class BuildconfigPluginTest {
    @TempDir
    lateinit var targetDir: File

    @Test
    fun testSimpleProjectBuildingWithKotlinDSL() {
        val extractedProject = extractTestProject("project1")
        assertDoesNotThrow {
            try {
                GradleRunner.create()
                    .withProjectDir(extractedProject)
                    .withArguments("build")
                    .withPluginClasspath()
                    .build()
            } catch (ex: Exception) {
                ex.printStackTrace()
                throw ex
            }
        }
    }

    @Test
    fun testSimpleProjectBuildingWithGroovyDSL() {
        val extractedProject = extractTestProject("project2")
        assertDoesNotThrow {
            try {
                GradleRunner.create()
                    .withProjectDir(extractedProject)
                    .withArguments("build")
                    .withPluginClasspath()
                    .build()
            } catch (ex: Exception) {
                ex.printStackTrace()
                throw ex
            }
        }
    }

    @Test
    fun testSimpleProjectWithOutput() {
        val extractedProject = extractTestProject("project3")
        assertDoesNotThrow {
            try {
                val output = GradleRunner.create()
                    .withProjectDir(extractedProject)
                    .withArguments("check")
                    .withPluginClasspath()
                    .build()
                    .output
                println(output)
                assertTrue(output.contains("Output 1: de.menkalian.vela"))
                assertTrue(output.contains("Output 2: test"))
                assertTrue(output.contains("Output 3: 69.42.alpha-3"))
            } catch (ex: Exception) {
                ex.printStackTrace()
                throw ex
            }
        }
    }

    private fun extractTestProject(name: String): File {
        val sourceFile = File(targetDir, "test.epc")
        sourceFile.createNewFile()
        sourceFile.writeBytes(
            BuildconfigPluginTest::class.java.classLoader.getResourceAsStream("de/menkalian/vela/test/gradle/$name.epc")!!.readAllBytes()
        )

        val extractionDir = File(targetDir, "test")
        extractionDir.mkdirs()
        Epc().decompress(sourceFile, extractionDir)
        return extractionDir
    }
}