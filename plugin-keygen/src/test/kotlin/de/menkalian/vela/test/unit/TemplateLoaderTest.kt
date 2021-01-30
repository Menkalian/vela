package de.menkalian.vela.test.unit

import de.menkalian.vela.plain.TemplateLoader
import org.gradle.internal.impldep.org.testng.AssertJUnit.assertEquals
import org.gradle.internal.impldep.org.testng.AssertJUnit.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Tag("unit")
internal class TemplateLoaderTest {
    class ModifiedTemplateLoader : TemplateLoader() {
        fun currentReplacements() = this.replacements
    }

    private lateinit var testLoader: ModifiedTemplateLoader

    @BeforeEach
    fun setUp() {
        testLoader = ModifiedTemplateLoader()
    }

    @Test
    fun initializeWithoutReplacements() {
        assertTrue("No replacements set by default", testLoader.currentReplacements().isEmpty())
    }

    @Test
    fun setSingleReplacement() {
        val newSetValue = "{{TEST}}" to "testvalue123"

        testLoader.setReplacement(newSetValue)
        assertTrue("Replacements set after adding one", testLoader.currentReplacements().isNotEmpty())
        assertEquals("Set value should be saved", "testvalue123", testLoader.currentReplacements()["{{TEST}}"])
        assertEquals("Should have set exactly one value", 1, testLoader.currentReplacements().size)
    }

    @Test
    fun setTwoReplacements() {
        val newSetValue = "*#-,.!1 üiö ²"

        testLoader.setReplacement("{{TEST}}" to "testvalue123")
        testLoader.setReplacement("{{TEST2}}" to newSetValue)
        assertEquals("Set value should be saved", newSetValue, testLoader.currentReplacements()["{{TEST2}}"])
        assertEquals("Should have set exactly two values", 2, testLoader.currentReplacements().size)
    }

    @Test
    fun replaceExistingReplacement() {
        val newSetValue = "twofourtyone"

        testLoader.setReplacement("{{TEST}}" to "testvalue123")
        testLoader.setReplacement("{{TEST2}}" to "*#-,.!1 üiö ²")
        testLoader.setReplacement("{{TEST}}" to newSetValue)
        assertEquals("Set value should be saved/not old value", newSetValue, testLoader.currentReplacements()["{{TEST}}"])
        assertEquals("Should have still exactly two values", 2, testLoader.currentReplacements().size)
    }

    @Test
    fun loadTemplateWithoutReplacements() {
        assertEquals(
            "Template should load without replacements",
            "There sould not be anything replaced here. {{TEMPLATE}} {{TEST}}",
            testLoader.loadTemplate("test1.txt")
        )
    }

    @Test
    fun loadTemplateWithReplacements() {
        testLoader.setReplacement("{{TEST}}" to "vela")
        testLoader.setReplacement("{{TEXT}}" to "menkalian")
        assertEquals(
            "Template should load with replacements",
            "vela menkalian vela\n" +
                    "{{TES T}} {{TEXXXT}}",
            testLoader.loadTemplate("test2.txt")
        )
    }

    @Test
    fun doNotLoadNonexistentTemplate() {
        assertThrows<NullPointerException>("Should not load invalid File") {
            testLoader.loadTemplate("test3.txt")
        }
    }

    @Test
    fun loadTemplateWithUncommonPlaceholders() {
        testLoader.setReplacement("**TEST**" to "vela")
        testLoader.setReplacement("--clazz--" to "1234")
        testLoader.setReplacement("²J€FF³" to "Jeff")
        assertEquals(
            "Template should load with strange placeholders",
            "vela 1234 Jeff1234 Jeffvelavela1234",
            testLoader.loadTemplate("test4.txt")
        )

    }
}