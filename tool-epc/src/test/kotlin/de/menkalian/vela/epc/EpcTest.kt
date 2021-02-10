@file:Suppress("DEPRECATION")

package de.menkalian.vela.epc

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

internal class EpcTest {

    @Test
    fun compress(@TempDir sourceDir: File, @TempDir targetDir: File, @TempDir checkTargetDir: File) {
        val javaFile = File(sourceDir, "HelloWorld.java")
        javaFile.createNewFile()
        javaFile.writeText(
            """
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello Java!");
                    }
                }
            """.trimIndent(), Charsets.UTF_8
        )
        val kotlinFile = File(sourceDir, "HelloWorld.kt")
        kotlinFile.createNewFile()
        kotlinFile.writeText(
            """
                fun main() {
                    println("Hello Kotlin!")
                }
            """.trimIndent(), Charsets.UTF_8
        )

        val testFile = File(targetDir, "epcTest.epc")
        Epc().compress(sourceDir, testFile)

        val compressedText = testFile.readText(Charsets.UTF_8)

        assertTrue(
            compressedText.contains(
                """
                fun main() {
                    println("Hello Kotlin!")
                }
            """.trimIndent()
            )
        )
        assertTrue(
            compressedText.contains(
                """
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello Java!");
                    }
                }
            """.trimIndent()
            )
        )

        // decompress and check it is still equal
        Epc().decompress(testFile, checkTargetDir)
        assertTrue(compareFiles(sourceDir, checkTargetDir))
    }

    @Test
    fun decompress(@TempDir sourceDir: File, @TempDir targetDir: File) {
        val testFile = File.createTempFile("epcTest", "epc")
        val testInputStream = EpcTest::class.java.classLoader.getResourceAsStream("test.epc")
        testFile.writeBytes(testInputStream!!.readAllBytes())

        val javaFile = File(sourceDir, "HelloWorld.java")
        javaFile.createNewFile()
        javaFile.writeText(
            """
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello Java!");
                    }
                }
            """.trimIndent(), Charsets.UTF_8
        )
        val kotlinFile = File(sourceDir, "HelloWorld.kt")
        kotlinFile.createNewFile()
        kotlinFile.writeText(
            """
                fun main() {
                    println("Hello Kotlin!")
                }
            """.trimIndent(), Charsets.UTF_8
        )

        Epc().decompress(testFile, targetDir)
        testFile.delete()
        assertTrue(compareFiles(sourceDir, targetDir))
    }
}