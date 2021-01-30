package de.menkalian.vela.test.integration

import de.menkalian.vela.gradle.KeyObjectExtension
import de.menkalian.vela.plain.ClassWriter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.random.Random

@Tag("integration")
class ClassWriterTest {
    lateinit var target: File
    lateinit var config: KeyObjectExtension
    lateinit var writer: ClassWriter

    @BeforeEach
    fun setUp(@TempDir tempDir: File) {
        target = tempDir
        target.mkdirs()

        config = KeyObjectExtension(target, target)
        config.targetDir = target.toURI()

        writer = ClassWriter("Test", "de.menkalian.vela.test", config)
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun testInitalWriting() {
        val testFile = File(target, "de/menkalian/vela/test/Test.java")
        assertTrue(testFile.exists(), "ClassFile should be created")
        assertTrue(testFile.length() > 0, "ClassFile should not be Empty")
        assertTrue(testFile.readText().startsWith("package de.menkalian.vela.test;"), "Package should be declared")
    }

    @Test
    fun writeText() {
        val testFile = File(target, "de/menkalian/vela/test/Test.java")
        val testText = "This is a Test Text with variable Symbols #äöü³²avmo¦$┌!/{}³{10fcj"
        writer.writeText(testText)
        assertTrue(testFile.readText().contains(testText), "Content should be written")
    }

    @Test
    fun testReplacementForOldFile() {
        val randomText = String(Random.Default.nextBytes(4096))
        writer.writeText(randomText)
        ClassWriter("Test", "de.menkalian.vela.test", config)

        val testFile = File(target, "de/menkalian/vela/test/Test.java")
        assertFalse(testFile.readText().contains(randomText), "Outdated Files should be replaced")
    }
}