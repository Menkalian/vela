package de.menkalian.vela.template.evaluator

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.GlobalNodeContext
import de.menkalian.vela.template.evaluator.node.leaf.TextNode
import de.menkalian.vela.template.evaluator.node.unary.VariableAccessNode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TemplateEvaluatorTest {
    @Test
    fun testTemplateEvaluator() {

    }

    @Test
    fun testNodeReplacement() {
        val xReplacement = GlobalNodeContext
            .dynamicReplacementEntries
            .first { it.key == "XXX" }

        Assertions.assertTrue(xReplacement.accessorNode is VariableAccessNode)
        Assertions.assertEquals("%03d", xReplacement.format)

        Assertions.assertEquals(
            "var.XXX.var",
            GlobalNodeContext.evaluateName("var.XXX.var", Variables())
        )

        GlobalNodeContext.addDynamicReplacementEntry("POS", TextNode("position"), "%s")

        Assertions.assertEquals(
            "var.position.var",
            GlobalNodeContext.evaluateName("var.POS.var", Variables())
        )
    }
}