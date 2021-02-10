package de.menkalian.vela.test.integration

import de.menkalian.vela.gradle.KeyObjectExtension
import de.menkalian.vela.plain.KeyGenerator
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File

@Suppress("SpellCheckingInspection")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class KeyGeneratorTest {
    private lateinit var source: File
    private lateinit var target: File
    private lateinit var extension: KeyObjectExtension

    @BeforeEach
    fun setup(@TempDir temp: File) {
        source = File(temp, "source")
        target = File(temp, "target")

        source.mkdirs()
        target.mkdirs()

        extension = KeyObjectExtension(target, source)
        extension.sourceDir = source.toURI()
        extension.targetDir = target.toURI()
    }

    // region test basic behaviour
    @Test
    fun testDefaultConfiguration() {
        val testFile = File(source, "Test.ckf")
        testFile.createNewFile()
        testFile.writeText(
            """
                Vela
                  Test
                    Keys
                    Keyzz
                  Prod
                    KeyGen
                    Versioning
            """.trimIndent()
        )
        KeyGenerator(extension).runGeneration()

        val baseKeyFile = File(target, "de/menkalian/vela/generated/GeneratedKeyBase.java")
        val velaKeyFile = File(target, "de/menkalian/vela/generated/VelaKey.java")

        assertTrue(baseKeyFile.exists())
        assertTrue(velaKeyFile.exists())

        val generatedCode = velaKeyFile.readText()
        assertTrue(generatedCode.contains("class VelaKey "))
        assertTrue(generatedCode.contains("class TestKey "))
        assertTrue(generatedCode.contains("class KeysKey "))
        assertTrue(generatedCode.contains("class ProdKey "))
        assertTrue(generatedCode.contains("public static VelaKey Vela"))
        assertTrue(generatedCode.contains("public KeyzzKey Keyzz"))
        assertTrue(generatedCode.contains("public KeyGenKey KeyGen"))
    }

    @Test
    fun testMultipleTopLevelKeysInOneFile() {
        val testFile = File(source, "Test.ckf")
        testFile.createNewFile()
        testFile.writeText(
            """
                Vela
                  Test
                    Keys
                    Keyzz
                Aquila
                  Test
                    Keys
                    Keyzz
            """.trimIndent()
        )
        KeyGenerator(extension).runGeneration()

        val baseKeyFile = File(target, "de/menkalian/vela/generated/GeneratedKeyBase.java")
        val velaKeyFile = File(target, "de/menkalian/vela/generated/VelaKey.java")
        val aquilaKeyFile = File(target, "de/menkalian/vela/generated/AquilaKey.java")

        assertTrue(baseKeyFile.exists())
        assertTrue(velaKeyFile.exists())
        assertTrue(aquilaKeyFile.exists())
    }

    @Test
    fun testMultipleFiles() {
        val testFile = File(source, "Vela.ckf")
        testFile.createNewFile()
        testFile.writeText(
            """
                Vela
                  Test
                    Keys
                    Keyzz
            """.trimIndent()
        )
        val testFile2 = File(source, "Aquila.ckf")
        testFile2.createNewFile()
        testFile2.writeText(
            """
                Aquila
                  Test
                    Keys
                    Keyzz
            """.trimIndent()
        )
        KeyGenerator(extension).runGeneration()

        val baseKeyFile = File(target, "de/menkalian/vela/generated/GeneratedKeyBase.java")
        val velaKeyFile = File(target, "de/menkalian/vela/generated/VelaKey.java")
        val aquilaKeyFile = File(target, "de/menkalian/vela/generated/AquilaKey.java")

        assertTrue(baseKeyFile.exists())
        assertTrue(velaKeyFile.exists())
        assertTrue(aquilaKeyFile.exists())
    }
    // endregion

    //region test different configurations
    @Test
    fun testTargetPackage() {
        val testFile = File(source, "Test.ckf")
        testFile.createNewFile()
        testFile.writeText(
            """
                Vela
                  Test
                    Keys
                    Keyzz
                  Prod
                    KeyGen
                    Versioning
            """.trimIndent()
        )

        extension.targetPackage = "test.otherdir.nodomain.keyzz"

        KeyGenerator(extension).runGeneration()

        val baseKeyFile = File(target, "test/otherdir/nodomain/keyzz/GeneratedKeyBase.java")
        val velaKeyFile = File(target, "test/otherdir/nodomain/keyzz/VelaKey.java")

        assertTrue(baseKeyFile.exists())
        assertTrue(velaKeyFile.exists())

        val generatedCode = velaKeyFile.readText()
        assertTrue(generatedCode.contains("package test.otherdir.nodomain.keyzz;"))
    }

    @Test
    fun testSeparator() {
        val testFile = File(source, "Test.ckf")
        testFile.createNewFile()
        testFile.writeText(
            """
                Vela
                  Test
                    Keys
                    Keyzz
                  Prod
                    KeyGen
                    Versioning
            """.trimIndent()
        )

        extension.separator = "~~"

        KeyGenerator(extension).runGeneration()

        val baseKeyFile = File(target, "de/menkalian/vela/generated/GeneratedKeyBase.java")

        val generatedCode = baseKeyFile.readText()
        assertTrue(generatedCode.contains("parent + \"~~\" + key"))
    }

    @Test
    fun testFinalAsString() {
        val testFile = File(source, "Test.ckf")
        testFile.createNewFile()
        testFile.writeText(
            """
                Vela
                  Test
                    Keys
                    Keyzz
                  Prod
                    KeyGen
                    Versioning
            """.trimIndent()
        )

        extension.finalLayerAsString = true

        KeyGenerator(extension).runGeneration()

        val velaKeyFile = File(target, "de/menkalian/vela/generated/VelaKey.java")

        val generatedCode = velaKeyFile.readText()
        assertTrue(generatedCode.contains("public String Versioning = toString() + \".Versioning\";"))
    }

    @Test
    fun testRecursiveScanning() {
        val testFile = File(source, "Vela.ckf")
        testFile.createNewFile()
        testFile.writeText(
            """
                Vela
                  Test
                    Keys
                    Keyzz
            """.trimIndent()
        )
        val testFile2 = File(source, "de/menkalian/aquila/Aquila.ckf")
        testFile2.parentFile.mkdirs()
        testFile2.createNewFile()
        testFile2.writeText(
            """
                Aquila
                  Test
                    Keys
                    Keyzz
            """.trimIndent()
        )

        val testFile3 = File(source, "de/menkalian/auriga/Auriga.ckf")
        testFile3.parentFile.mkdirs()
        testFile3.createNewFile()
        testFile3.writeText(
            """
                Auriga
                  Test
                    Keys
                    Keyzz
            """.trimIndent()
        )

        extension.scanRecursive = true

        KeyGenerator(extension).runGeneration()

        val baseKeyFile = File(target, "de/menkalian/vela/generated/GeneratedKeyBase.java")
        val velaKeyFile = File(target, "de/menkalian/vela/generated/VelaKey.java")
        val aquilaKeyFile = File(target, "de/menkalian/vela/generated/AquilaKey.java")
        val aurigaKeyFile = File(target, "de/menkalian/vela/generated/AurigaKey.java")

        assertTrue(baseKeyFile.exists())
        assertTrue(velaKeyFile.exists())
        assertTrue(aquilaKeyFile.exists())
        assertTrue(aurigaKeyFile.exists())
    }

    @Test
    fun testRecursivePrefixing() {
        val testFile = File(source, "De/Menkalian/Auriga.ckf")
        testFile.parentFile.mkdirs()
        testFile.createNewFile()
        testFile.writeText(
            """
                Auriga
                  Test
                    Keys
                    Keyzz
            """.trimIndent()
        )

        val testFile2 = File(source, "De/Menkalian/Aquila/Aquila.ckf")
        testFile2.parentFile.mkdirs()
        testFile2.createNewFile()
        testFile2.writeText(
            """
                Test
                  Keys
                  Keyzz
            """.trimIndent()
        )

        extension.scanRecursive = true
        extension.prefixRecursive = true

        KeyGenerator(extension).runGeneration()

        val aquilaKeyFile = File(target, "de/menkalian/vela/generated/TestKey.java")
        val aurigaKeyFile = File(target, "de/menkalian/vela/generated/AurigaKey.java")

        assertTrue(aquilaKeyFile.readText().contains("super(\"Test\", \"De.Menkalian.Aquila\");"))
        assertTrue(aurigaKeyFile.readText().contains("super(\"Auriga\", \"De.Menkalian\");"))
    }
    //endregion
}