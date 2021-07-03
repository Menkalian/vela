package de.menkalian.vela.template.evaluator.node.leaf

import de.menkalian.vela.template.Variables

/**
 * Simple node that encapsulates all plain text.
 * Does no evaluation, just plainly returns the text.
 */
class TextNode(val textValue: String) : ILeafNode {
    override fun getValue(variables: Variables): String = textValue
}