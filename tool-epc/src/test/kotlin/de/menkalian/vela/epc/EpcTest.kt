@file:Suppress("DEPRECATION")

package de.menkalian.vela.epc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

internal class EpcTest {

    @Test
    fun compress() {
        val sourceDir = createTempDir("epcTestExpected")
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

        val testFile = File.createTempFile("epcTest", "epc")
        Epc().compress(sourceDir, testFile)
        assertEquals(
            String(EpcTest::class.java.classLoader.getResourceAsStream("test.epc")!!.readAllBytes(), Charsets.UTF_8),
            testFile.readText(Charsets.UTF_8)
        )
    }

    @Test
    fun decompress() {
        val testFile = File.createTempFile("epcTest", "epc")
        val testInputStream = EpcTest::class.java.classLoader.getResourceAsStream("test.epc")
        testFile.writeBytes(testInputStream!!.readAllBytes())

        val expextedDir = createTempDir("epcTestExpected")
        val javaFile = File(expextedDir, "HelloWorld.java")
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
        val kotlinFile = File(expextedDir, "HelloWorld.kt")
        kotlinFile.createNewFile()
        kotlinFile.writeText(
            """
                fun main() {
                    println("Hello Kotlin!")
                }
            """.trimIndent(), Charsets.UTF_8
        )

        val targetDir = createTempDir("epcTest")
        Epc().decompress(testFile, targetDir)
        assertTrue(compareFiles(expextedDir, targetDir))
    }
}