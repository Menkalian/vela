package de.menkalian.vela.test.functional

import de.menkalian.vela.epc.Epc
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.FileWriter

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class KeygenPluginTest {
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
                    .forwardOutput()
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
                    .forwardOutput()
                    .build()
            } catch (ex: Exception) {
                ex.printStackTrace()
                throw ex
            }
        }
    }

    @Test
    fun testProjectWithTests() {
        val extractedProject = extractTestProject("project3")
        assertDoesNotThrow {
            try {
                GradleRunner.create()
                    .withProjectDir(extractedProject)
                    .withArguments("check")
                    .withPluginClasspath()
                    .forwardOutput()
                    .build()
            } catch (ex: Exception) {
                ex.printStackTrace()
                throw ex
            }
        }
    }

    @Test
    fun testSimpleProjectBuildingWithCppKeys() {
        val extractedProject = extractTestProject("project4")
        assertDoesNotThrow {
            try {
                GradleRunner.create()
                    .withProjectDir(extractedProject)
                    .withArguments("generateKeyObjects")
                    .withPluginClasspath()
                    .forwardOutput()
                    .build()
            } catch (ex: Exception) {
                ex.printStackTrace()
                throw ex
            }
        }
    }

    private fun extractTestProject(name: String): File {
        val sourceFile = File(targetDir, "test.epc")
        targetDir.mkdirs()
        sourceFile.createNewFile()
        sourceFile.writeBytes(
            KeygenPluginTest::class.java.classLoader.getResourceAsStream("de/menkalian/vela/test/functional/$name.epc")!!.readAllBytes()
        )

        val extractionDir = File(targetDir, "test")
        extractionDir.mkdirs()
        Epc().decompress(sourceFile, extractionDir)
        return extractionDir
    }
}