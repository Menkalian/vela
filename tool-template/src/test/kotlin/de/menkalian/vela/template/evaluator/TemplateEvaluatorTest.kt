package de.menkalian.vela.template.evaluator

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.GlobalNodeContext
import de.menkalian.vela.template.evaluator.node.leaf.TextNode
import de.menkalian.vela.template.evaluator.node.unary.VariableAccessNode
import de.menkalian.vela.template.parser.ITemplateParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TemplateEvaluatorTest {
    @Test
    fun testTemplateEvaluator() {
        val source = """
            Lets generate the even numbers from 4 to 19.
            
            {FOR {I} IN {4} TO {19} {&OFF{
            &SET{TMP}{&STR_END{I}{1}}
            }{IF 
                {&OR{&IS_EQUAL{0}{{{TMP}}}}{&OR{&IS_EQUAL{2}{{{TMP}}}}{&OR{&IS_EQUAL{4}{{{TMP}}}}{&OR{&IS_EQUAL{6}{{{TMP}}}}{&IS_EQUAL{8}{{{TMP}}}}}}}}
                {{{I}}{IF {&NOT{&IS_EQUAL{{{I}}}{19}}} {, }}}
            }}}
        """.trimIndent()

        val expected = """
            Lets generate the even numbers from 4 to 19.
            
            4, 6, 8, 10, 12, 14, 16, 18, 
        """.trimIndent()

        val output = ITemplateParser
            .getDefaultImplementation()
            .parse(source)
            .evaluate()
        Assertions.assertEquals(expected, output)
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