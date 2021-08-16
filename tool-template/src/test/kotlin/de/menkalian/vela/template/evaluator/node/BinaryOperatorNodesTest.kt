package de.menkalian.vela.template.evaluator.node

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.interpretAsBoolean
import de.menkalian.vela.template.evaluator.node.binary.AndOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.ConcatOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.IsEqualOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.IsGreaterEqualOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.IsGreaterOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.IsLessEqualOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.IsLessOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.OrOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.SetOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.StringEndOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.StringStartOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.WhileLoopNode
import de.menkalian.vela.template.evaluator.node.binary.XorOperatorNode
import de.menkalian.vela.template.evaluator.node.leaf.TextNode
import de.menkalian.vela.template.evaluator.node.multi.ConcatenationNode
import de.menkalian.vela.template.evaluator.node.unary.DefinedOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.IncrementOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.NotOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.VariableAccessNode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class BinaryOperatorNodesTest {
    // Control Structures
    @Test
    fun testWhileLoopNode() {
        val variables = Variables()
        val expectedVariables = Variables()
        expectedVariables["Vela.Test.Variable.001"] = "1"
        expectedVariables["Vela.Test.Variable.002"] = "2"
        expectedVariables["Vela.Test.Variable.003"] = "3"
        expectedVariables["X"] = "3"

        // create the node for testing
        val whileNode = WhileLoopNode(
            NotOperatorNode(DefinedOperatorNode(TextNode("Vela.Test.Variable.003"))),
            ConcatenationNode(
                listOf(
                    IncrementOperatorNode(TextNode("X")),
                    SetOperatorNode(TextNode("Vela.Test.Variable.XXX"), VariableAccessNode("X")),
                    VariableAccessNode("X"),
                    TextNode(": "),
                    VariableAccessNode("Vela.Test.Variable.XXX"),
                    TextNode("\n")
                )
            )
        )

        // While Node has no return Value
        Assertions.assertThrows(RuntimeException::class.java) { whileNode.getValue(variables) }

        val tmp = StringBuilder()
        whileNode.evaluate(variables, tmp)
        Assertions.assertEquals("1: 1\n2: 2\n3: 3\n", tmp.toString())
        Assertions.assertEquals(expectedVariables, variables)
    }

    // Operators
    @Test
    fun testConcatOperatorNode() {
        val testSequence = ConcatenationNode(
            listOf(
                SetOperatorNode(TextNode("Test.Value.001"), TextNode("g4299c")),
                SetOperatorNode(TextNode("Test.Value.002"), TextNode("0RY")),
                SetOperatorNode(TextNode("Test.Value.003"), VariableAccessNode("Test.Value.001")),
                ConcatOperatorNode(TextNode("Test.Value.003"), VariableAccessNode("Test.Value.002"))
            )
        )

        val result = StringBuilder()
        val startVars = Variables()

        testSequence.evaluate(startVars, result)
        Assertions.assertEquals("g4299c0RY", result.toString())
        Assertions.assertEquals("g4299c0RY", startVars["Test.Value.003"])
    }

    @Test
    fun testSetOperatorNode() {
        val testNode = SetOperatorNode(TextNode("Test.Value"), TextNode("8D6"))

        val result = StringBuilder()
        val startVars = Variables()

        testNode.evaluate(startVars, result)
        Assertions.assertEquals("8D6", startVars["Test.Value"])
    }

    // Logical Operators
    @Test
    fun testAndOperatorNode() {
        val check = { o1: Boolean, o2: Boolean, res: Boolean ->
            val testNode = AndOperatorNode(
                TextNode(o1.toString()),
                TextNode(o2.toString())
            )

            Assertions.assertEquals(res, testNode.getValue(Variables()).interpretAsBoolean())
        }

        check(false, false, false)
        check(false, true, false)
        check(true, false, false)
        check(true, true, true)
    }

    @Test
    fun testOrOperatorNode() {
        val check = { o1: Boolean, o2: Boolean, res: Boolean ->
            val testNode = OrOperatorNode(
                TextNode(o1.toString()),
                TextNode(o2.toString())
            )

            Assertions.assertEquals(res, testNode.getValue(Variables()).interpretAsBoolean())
        }

        check(false, false, false)
        check(false, true, true)
        check(true, false, true)
        check(true, true, true)
    }

    @Test
    fun testXorOperatorNode() {
        val check = { o1: Boolean, o2: Boolean, res: Boolean ->
            val testNode = XorOperatorNode(
                TextNode(o1.toString()),
                TextNode(o2.toString())
            )

            Assertions.assertEquals(res, testNode.getValue(Variables()).interpretAsBoolean())
        }

        check(false, false, false)
        check(false, true, true)
        check(true, false, true)
        check(true, true, false)
    }

    // Compare Operators

    @Test
    fun testIsEqualOperatorNode() {
        val check = { o1: String, o2: String, res: Boolean ->
            val testNode = IsEqualOperatorNode(
                TextNode(o1),
                TextNode(o2)
            )

            Assertions.assertEquals(res, testNode.getValue(Variables()).interpretAsBoolean())
        }

        check("1", "3", false)
        check("1353oo3", "hLow1mc", false)
        check("8DOlz7GY", "8DOlz7GY", true)
        check("4", "4", true)
    }

    @Test
    fun testIsGreaterEqualOperatorNode() {
        val check = { o1: String, o2: String, res: Boolean ->
            val testNode = IsGreaterEqualOperatorNode(
                TextNode(o1),
                TextNode(o2)
            )

            Assertions.assertEquals(res, testNode.getValue(Variables()).interpretAsBoolean())
        }

        check("1", "3", false)
        check("3", "1", true)
        check("4", "4", true)
        check("1353oo3", "hLow1mc", false)
        check("hLow1mc", "1353oo3", true)
        check("8DOlz7GY", "8DOlz7GY", true)
    }

    @Test
    fun testIsGreaterOperatorNode() {
        val check = { o1: String, o2: String, res: Boolean ->
            val testNode = IsGreaterOperatorNode(
                TextNode(o1),
                TextNode(o2)
            )

            Assertions.assertEquals(res, testNode.getValue(Variables()).interpretAsBoolean())
        }

        check("1", "3", false)
        check("3", "1", true)
        check("4", "4", false)
        check("1353oo3", "hLow1mc", false)
        check("hLow1mc", "1353oo3", true)
        check("8DOlz7GY", "8DOlz7GY", false)
    }

    @Test
    fun testIsLessEqualOperatorNode() {
        val check = { o1: String, o2: String, res: Boolean ->
            val testNode = IsLessEqualOperatorNode(
                TextNode(o1),
                TextNode(o2)
            )

            Assertions.assertEquals(res, testNode.getValue(Variables()).interpretAsBoolean())
        }

        check("1", "3", true)
        check("3", "1", false)
        check("4", "4", true)
        check("1353oo3", "hLow1mc", true)
        check("hLow1mc", "1353oo3", false)
        check("8DOlz7GY", "8DOlz7GY", true)
    }

    @Test
    fun testIsLessOperatorNode() {
        val check = { o1: String, o2: String, res: Boolean ->
            val testNode = IsLessOperatorNode(
                TextNode(o1),
                TextNode(o2)
            )

            Assertions.assertEquals(res, testNode.getValue(Variables()).interpretAsBoolean())
        }

        check("1", "3", true)
        check("3", "1", false)
        check("4", "4", false)
        check("1353oo3", "hLow1mc", true)
        check("hLow1mc", "1353oo3", false)
        check("8DOlz7GY", "8DOlz7GY", false)
    }

    @Test
    fun testStringStartOperatorNode() {
        val check = { string: String, arg: Int, res: String ->
            val testNode = StringStartOperatorNode(
                TextNode(string),
                TextNode(arg.toString())
            )

            Assertions.assertEquals(res, testNode.getValue(Variables()))
        }

        check("3vVim2y", 9, "3vVim2y")
        check("zAsJ8q", 2, "zA")
        check("zAsJ8q", 5, "zAsJ8")
        check("zAsJ8q", 0, "")
    }

    @Test
    fun testStringEndOperatorNode() {
        val check = { string: String, arg: Int, res: String ->
            val testNode = StringEndOperatorNode(
                TextNode(string),
                TextNode(arg.toString())
            )

            Assertions.assertEquals(res, testNode.getValue(Variables()))
        }

        check("3vVim2y", 9, "3vVim2y")
        check("zAsJ8q", 2, "8q")
        check("zAsJ8q", 5, "AsJ8q")
        check("zAsJ8q", 0, "")
    }
}