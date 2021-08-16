package de.menkalian.vela.template.evaluator.node

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.leaf.TextNode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class LeafNodeTest {
    @Test
    fun testTextNode() {
        val testValue =  { text : String ->
            val node = TextNode(text)
            val tmp = StringBuilder()
            node.evaluate(Variables(), tmp)
            Assertions.assertEquals(text, node.textValue)
            Assertions.assertEquals(text, tmp.toString())
            Assertions.assertEquals(text, node.getValue(Variables()))
        }

        testValue("ujK")
        testValue("wreck")
        testValue("9ARPx")
    }
}