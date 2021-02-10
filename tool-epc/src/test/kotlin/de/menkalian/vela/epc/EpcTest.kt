@file:Suppress("DEPRECATION")

package de.menkalian.vela.epc

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

internal class EpcTest {

    @Test
    fun compress(@TempDir sourceDir: File, @TempDir checkTargetDir: File) {
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

        val testFile = Epc().compress(sourceDir)

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

    @Test
    fun checkUnrecognizedFiletype(@TempDir sourceDir: File) {
        val file = File(sourceDir, "file.noKnownExtension")
        file.createNewFile()
        file.writeText("TESTTEXT123456")

        val testFile = Epc(defaultStoreType = StoreType.TEXT).compress(sourceDir)
        assertTrue(testFile.readText().contains("type=TEXT"))
        testFile.delete()

        val testFile2 = Epc(defaultStoreType = StoreType.BINARY).compress(sourceDir)
        assertTrue(testFile2.readText().contains("type=BINARY"))
        testFile2.delete()
    }

    @Test
    fun testBinaryDecompression(@TempDir sourceDir: File, @TempDir targetDir: File) {
        val array = byteArrayOf(
            15, -10, 81, 125, -128, -1, 17
        )

        val file = File(sourceDir, "file.bin")
        file.createNewFile()
        file.writeBytes(array)

        val epc = Epc()
        val testFile = epc.compress(sourceDir)
        epc.decompress(testFile, targetDir)

        assertTrue(compareFiles(sourceDir, targetDir))
    }
}