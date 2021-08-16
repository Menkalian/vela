package de.menkalian.vela.template.evaluator.node

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.interpretAsBoolean
import de.menkalian.vela.template.evaluator.node.binary.SetOperatorNode
import de.menkalian.vela.template.evaluator.node.leaf.TextNode
import de.menkalian.vela.template.evaluator.node.multi.ConcatenationNode
import de.menkalian.vela.template.evaluator.node.unary.AddSpacerOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.ClearOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.DecrementOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.DefinedOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.IncrementOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.IsBooleanOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.IsNumericOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.NotOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.RefOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.RemoveSpacerOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.StringLengthOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.StringLowercaseOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.StringUppercaseOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.VariableAccessNode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class UnaryOperatorNodesTest {
    // Control Structures
    @Test
    fun testVariableAccessNode() {
        val testSequence = ConcatenationNode(listOf(
            SetOperatorNode(TextNode("Test.Value"), TextNode("KX92EcK")),
            VariableAccessNode("Test.Value")
        ))

        val result = StringBuilder()
        val startVars = Variables()

        testSequence.evaluate(startVars, result)
        Assertions.assertEquals("KX92EcK", result.toString())
    }

    // Operators
    // Variable manipulation
    @Test
    fun testClearOperatorNode() {
        val testSequence = ConcatenationNode(listOf(
            SetOperatorNode(TextNode("Test.Value"), TextNode("KX92EcK")),
            ClearOperatorNode(TextNode("Test.Value"))
        ))

        val result = StringBuilder()
        val startVars = Variables()

        testSequence.evaluate(startVars, result)
        Assertions.assertTrue(startVars.containsKey("Test.Value").not())
    }

    @Test
    fun testIncrementOperatorNode() {
        val testSequence = ConcatenationNode(listOf(
            SetOperatorNode(TextNode("X"), TextNode("5")),
            IncrementOperatorNode(TextNode("X"))
        ))

        val result = StringBuilder()
        val startVars = Variables()

        testSequence.evaluate(startVars, result)
        Assertions.assertEquals("6", startVars["X"])
    }

    @Test
    fun testDecrementOperatorNode() {
        val testSequence = ConcatenationNode(listOf(
            SetOperatorNode(TextNode("X"), TextNode("5")),
            DecrementOperatorNode(TextNode("X"))
        ))

        val result = StringBuilder()
        val startVars = Variables()

        testSequence.evaluate(startVars, result)
        Assertions.assertEquals("4", startVars["X"])
    }

    // Spacer manipulation operators
    @Test
    fun testAddSpacerOperatorNode() {
        val testSequence = ConcatenationNode(listOf(
            SetOperatorNode(TextNode("X"), TextNode("5")),
            AddSpacerOperatorNode(TextNode("_")),
            SetOperatorNode(TextNode("Test_Value_XXX"), TextNode("YEP"))
        ))

        val result = StringBuilder()
        val startVars = Variables()

        testSequence.evaluate(startVars, result)
        Assertions.assertTrue(GlobalNodeContext.separators.contains("_"))
        Assertions.assertTrue(startVars.containsKey("Test_Value_005"))
        Assertions.assertEquals("YEP", startVars["Test_Value_005"])

        GlobalNodeContext.clear()
    }

    @Test
    fun testRemoveSpacerOperatorNode() {
        val testSequence = ConcatenationNode(listOf(
            SetOperatorNode(TextNode("X"), TextNode("5")),
            AddSpacerOperatorNode(TextNode("_")),
            RemoveSpacerOperatorNode(TextNode("_")),
            SetOperatorNode(TextNode("Test_Value_XXX"), TextNode("YEP"))
        ))

        val result = StringBuilder()
        val startVars = Variables()

        testSequence.evaluate(startVars, result)
        Assertions.assertFalse(GlobalNodeContext.separators.contains("_"))
        Assertions.assertFalse(startVars.containsKey("Test_Value_005"))
        Assertions.assertEquals("YEP", startVars["Test_Value_XXX"])

        GlobalNodeContext.clear()
    }

    // Meta operators
    @Test
    fun testRefOperatorNode() {
        val testSequence = ConcatenationNode(listOf(
            SetOperatorNode(TextNode("Test.Ref"), TextNode("Test.Value")),
            SetOperatorNode(TextNode("Test.Value"), TextNode("YEP")),
            RefOperatorNode(TextNode("Test.Ref"))
        ))

        val result = StringBuilder()
        val startVars = Variables()

        testSequence.evaluate(startVars, result)
        Assertions.assertEquals("YEP", result.toString())
    }

    @Test
    fun testDefinedOperatorNode() {
        val startVars = Variables()
        val check = {str: String, res: Boolean ->
            val testNode = DefinedOperatorNode(
                TextNode(str)
            )

            Assertions.assertEquals(res, testNode.getValue(startVars).interpretAsBoolean())
        }

        startVars["Boolean.Value.True"] = "true"
        startVars["Boolean.Value.False.002"] = "0"
        startVars["No.Boolean"] = "TOToeuh7"

        check("Boolean.Value.True", true)
        check("Boolean.Value.False", false)
        check("Boolean.Value.True.002", false)
        check("Boolean.Value.False.002", true)
        check("No.Boolean", true)
    }

    @Test
    fun testIsBooleanOperatorNode() {
        val startVars = Variables()
        val check = {str: String, res: Boolean ->
            val testNode = IsBooleanOperatorNode(
                VariableAccessNode(str)
            )

            Assertions.assertEquals(res, testNode.getValue(startVars).interpretAsBoolean())
        }

        startVars["Boolean.Value.True"] = "true"
        startVars["Boolean.Value.False"] = "false"
        startVars["Boolean.Value.True.002"] = "1"
        startVars["Boolean.Value.False.002"] = "0"
        startVars["No.Boolean"] = "TOToeuh7"

        check("Boolean.Value.True", true)
        check("Boolean.Value.False", true)
        check("Boolean.Value.True.002", true)
        check("Boolean.Value.False.002", true)
        check("No.Boolean", false)
    }

    @Test
    fun testIsNumericOperatorNode() {
        val startVars = Variables()
        val check = {str: String, res: Boolean ->
            val testNode = IsNumericOperatorNode(
                TextNode(str)
            )

            Assertions.assertEquals(res, testNode.getValue(startVars).interpretAsBoolean())
        }

        check("842", true)
        check("417", true)
        check("apviaow", false)
        check("qeartghe242", false)
        check("c9da4f7f-1027-4c90-a464-cf359e38714e", false)
    }

    // Logical operators
    @Test
    fun testNotOperatorNode() {
        val check = { o1: Boolean, res: Boolean ->
            val testNode = NotOperatorNode(
                TextNode(o1.toString())
            )

            Assertions.assertEquals(res, testNode.getValue(Variables()).interpretAsBoolean())
        }

        check(false, true)
        check(true, false)
    }

    // String operators
    @Test
    fun testStringLengthOperator() {
        val check = { o1: String, res: Int ->
            val testNode = StringLengthOperatorNode(
                TextNode(o1)
            )

            Assertions.assertEquals(res, testNode.getValue(Variables()).toInt())
        }

        check("H3fuu", 5)
        check("", 0)
        check("aaaaaaa", 7)
        check("aaa\u0000aaaa", 8)
    }

    @Test
    fun testStringLowercaseOperator() {
        val check = { o1: String, res: String ->
            val testNode = StringLowercaseOperatorNode(
                TextNode(o1)
            )

            Assertions.assertEquals(res, testNode.getValue(Variables()))
        }

        check("H3fuu", "h3fuu")
        check("", "")
        check("aaaaaaa", "aaaaaaa")
        check("aaa\u0000aaaa", "aaa\u0000aaaa")
        check("aaa12AADÄS", "aaa12aadäs")
    }

    @Test
    fun testStringUppercaseOperator() {
        val check = { o1: String, res: String ->
            val testNode = StringUppercaseOperatorNode(
                TextNode(o1)
            )

            Assertions.assertEquals(res, testNode.getValue(Variables()))
        }

        check("H3fuu", "H3FUU")
        check("", "")
        check("aaaaaaa", "AAAAAAA")
        check("aaa\u0000aaaa", "AAA\u0000AAAA")
        check("aaa12AADÄS", "AAA12AADÄS")
    }
}