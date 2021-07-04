package de.menkalian.vela.template.evaluator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class UtilTest {
    @Test
    fun testInterpretAsBoolean() {
        Assertions.assertTrue("true".interpretAsBoolean())
        Assertions.assertTrue("YEP".interpretAsBoolean())
        Assertions.assertTrue("1".interpretAsBoolean())
        Assertions.assertFalse("0".interpretAsBoolean())
        Assertions.assertFalse("false".interpretAsBoolean())
        Assertions.assertFalse("FALSE".interpretAsBoolean())
        Assertions.assertFalse("fAlsE".interpretAsBoolean())
        Assertions.assertTrue("tRue".interpretAsBoolean())
        Assertions.assertTrue("764".interpretAsBoolean())
        Assertions.assertTrue("Z026".interpretAsBoolean())
    }
}