package de.menkalian.vela.template.evaluator.node

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.binary.IsEqualOperatorNode
import de.menkalian.vela.template.evaluator.node.binary.SetOperatorNode
import de.menkalian.vela.template.evaluator.node.leaf.TextNode
import de.menkalian.vela.template.evaluator.node.multi.ConcatenationNode
import de.menkalian.vela.template.evaluator.node.multi.ForLoopNode
import de.menkalian.vela.template.evaluator.node.multi.IfElseNode
import de.menkalian.vela.template.evaluator.node.multi.UseOperatorNode
import de.menkalian.vela.template.evaluator.node.unary.VariableAccessNode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class MultiOperatorNodeTest {
    // Control Structures
    @Test
    fun testForLoopNode() {
        val testNode = ForLoopNode(
            TextNode("X"),
            TextNode("4"),
            VariableAccessNode("Test.Value"),
            ConcatenationNode(
                listOf(
                    VariableAccessNode("X"),
                    TextNode("\n")
                )
            )
        )

        val result = StringBuilder()
        val startVars = Variables()
        startVars["Test.Value"] = "8"

        testNode.evaluate(startVars, result)

        Assertions.assertEquals("8", startVars["Test.Value"])
        Assertions.assertTrue(startVars.containsKey("X").not())
        Assertions.assertEquals("4\n5\n6\n7\n8\n", result.toString())
    }

    @Test
    fun testIfElseNode() {
        val testNode = IfElseNode(
            IsEqualOperatorNode(TextNode("L40aC"), VariableAccessNode("Test.Value")),
            TextNode("YEP"),
            TextNode("NOP")
        )

        val result = StringBuilder()
        val startVars = Variables()

        startVars["Test.Value"] = "L40aC"
        testNode.evaluate(startVars, result)
        Assertions.assertEquals("YEP", result.toString())
        result.clear()

        startVars["Test.Value"] = "17EXC2"
        testNode.evaluate(startVars, result)
        Assertions.assertEquals("NOP", result.toString())
    }

    @Test
    fun testConcatenationNode() {
        val testNode = ConcatenationNode(listOf(
            TextNode("5IF"),
            TextNode("v9CfC"),
            TextNode("qO7tu6")
        ))

        val result = StringBuilder()
        val startVars = Variables()

        testNode.evaluate(startVars, result)
        Assertions.assertEquals("5IFv9CfCqO7tu6", result.toString())
    }

    // Operators
    @Test
    fun testUseOperatorNode() {
        val testSequence = ConcatenationNode(listOf(
            SetOperatorNode(TextNode("Test.001.Value"), TextNode("AYX")),
            SetOperatorNode(TextNode("Test.LLL.Value"), TextNode("aa7libDh")),
            SetOperatorNode(TextNode("Test.L"), TextNode("1")),
            VariableAccessNode("Test.LLL.Value"),
            UseOperatorNode(TextNode("LLL"), VariableAccessNode("Test.L"), TextNode("%03d")),
            VariableAccessNode("Test.LLL.Value"),
        ))

        val result = StringBuilder()
        val startVars = Variables()

        testSequence.evaluate(startVars, result)
        Assertions.assertEquals("aa7libDhAYX", result.toString())
    }
}