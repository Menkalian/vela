package de.menkalian.vela.test.functional

import ch.vorburger.mariadb4j.DB
import ch.vorburger.mariadb4j.DBConfigurationBuilder
import com.github.stefanbirkner.systemlambda.SystemLambda
import de.menkalian.vela.epc.Epc
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.io.TempDir
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class VersioningPluginTest {
    @TempDir
    lateinit var targetDir: File

    @Test
    fun testProjectWithDatabase() {
        val extractedProject = extractTestProject("project1")
        val db = DB.newEmbeddedDB(
            DBConfigurationBuilder.newBuilder()
                .addArg("--user=root")
                .build()
        )
        db.start()
        db.createDB("vela_versioning")

        SystemLambda
            .withEnvironmentVariable("VELA_DB_HOST", "localhost")
            .and("VELA_DB_PORT", db.configuration.port.toString())
            .execute {
                assertDoesNotThrow {
                    val result = GradleRunner.create()
                        .withProjectDir(extractedProject)
                        .withArguments("build")
                        .withPluginClasspath()
                        .build()

                    assertTrue(result.output.contains("BuildNo: 1"))
                }
            }

        db.stop()
    }

    @Test
    fun testProjectWithPropFile() {
        val extractedProject = extractTestProject("project1")
        val propFile = File(extractedProject, "versioning.properties")
        propFile.writeText("BUILD_NO=7")

        assertDoesNotThrow {
            val result = GradleRunner.create()
                .withProjectDir(extractedProject)
                .withArguments("build")
                .withPluginClasspath()
                .build()

            assertTrue(result.output.contains("BuildNo: 8"))
            assertTrue(propFile.readText().contains("BUILD_NO=8"))
        }
    }

    @Test
    fun testBuildIncrementation() {
        val extractedProject = extractTestProject("project1")

        val propFile = File(extractedProject, "versioning.properties")
        propFile.writeText("BUILD_NO=71")

        assertDoesNotThrow {
            val build = GradleRunner.create()
                .withProjectDir(extractedProject)
                .withArguments("test")
                .withPluginClasspath()
                .build()
            assertTrue(build.output.contains("BuildNo: 72"))
        }

        assertDoesNotThrow {
            val output = GradleRunner.create()
                .withProjectDir(extractedProject)
                .withArguments("clean", "build")
                .withPluginClasspath()
                .build().output
            println(output)
            assertTrue(output.contains("BuildNo: 72"))
            assertTrue(propFile.readText().contains("BUILD_NO=72"))
        }

        SystemLambda
            .withEnvironmentVariable("VELA_NO_UPGRADE", "")
            .execute {
                assertDoesNotThrow {
                    val output = GradleRunner.create()
                        .withProjectDir(extractedProject)
                        .withArguments("clean", "build")
                        .withPluginClasspath()
                        .build().output
                    assertTrue(output.contains("BuildNo: 73"))
                    assertTrue(propFile.readText().contains("BUILD_NO=72"))
                }
            }
    }

    private fun extractTestProject(name: String): File {
        val sourceFile = File(targetDir, "test.epc")
        sourceFile.createNewFile()
        sourceFile.writeBytes(
            VersioningPluginTest::class.java.classLoader.getResourceAsStream("de/menkalian/vela/test/functional/$name.epc")!!.readAllBytes()
        )

        val extractionDir = File(targetDir, "test")
        extractionDir.mkdirs()
        Epc().decompress(sourceFile, extractionDir)
        return extractionDir
    }
}
