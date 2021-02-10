package de.menkalian.vela.test.functional

import de.menkalian.vela.epc.Epc
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.io.TempDir
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class KeygenTaskTest {
    @TempDir
    lateinit var targetDir: File

    @Test
    fun testSimpleProjectBuildingWithKotlinDSL() {
        val extractedProject = extractTestProject("project1")
        assertDoesNotThrow {
            GradleRunner.create()
                .withProjectDir(extractedProject)
                .withArguments("build")
                .withPluginClasspath()
                .build()
        }
    }

    @Test
    fun testSimpleProjectBuildingWithGroovyDSL() {
        val extractedProject = extractTestProject("project3")
        assertDoesNotThrow {
            GradleRunner.create()
                .withProjectDir(extractedProject)
                .withArguments("build")
                .withPluginClasspath()
                .build()
        }
    }

    @Test
    fun testAdvancedProjectWorking() {
        val extractedProject = extractTestProject("project2")
        val result = GradleRunner.create()
            .withProjectDir(extractedProject)
            .withArguments("check")
            .withPluginClasspath()
            .build()

        println(result.output)
        assertTrue(result.output.contains("Output 1: Vela.Test.Unit"))
        assertTrue(result.output.contains("Output 2: path/to/resource"))
        assertTrue(result.output.contains("Output 3: Test.Vela.Past"))
        assertTrue(result.output.contains("Output 4: De.Menkalian.Auriga.Plugin.Gradle"))
        assertTrue(result.output.contains("Output 5: Direct:Generated:String"))
        assertTrue(result.output.contains("Output 6: java.lang.String"))
        assertTrue(result.output.contains("Kt-Output 1: Vela.Test.Key"))
        assertTrue(result.output.contains("Kt-Output 2: path/to/articles"))
        assertTrue(result.output.contains("Kt-Output 3: Test.Vela.Future"))
        assertTrue(result.output.contains("Kt-Output 4: De.Menkalian.Auriga.Plugin.Maven"))
        assertTrue(result.output.contains("Kt-Output 5: Direct:Value"))
        assertTrue(result.output.contains("Kt-Output 6: java.lang.String"))
    }

    private fun extractTestProject(name: String): File {
        val sourceFile = File(targetDir, "test.epc")
        sourceFile.createNewFile()
        sourceFile.writeBytes(
            KeygenTaskTest::class.java.classLoader.getResourceAsStream("de/menkalian/vela/test/functional/$name.epc")!!.readAllBytes()
        )

        val extractionDir = File(targetDir, "test")
        extractionDir.mkdirs()
        Epc().decompress(sourceFile, extractionDir)
        return extractionDir
    }
}