package de.menkalian.vela.template.evaluator.node.multi

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.INode

open class ConcatenationNode(override val operands: List<INode>) : IMultiOperatorNode {
    override fun evaluate(variables: Variables, result: StringBuilder) {
        operands.forEach { it.evaluate(variables, result) }
    }

    override fun getValue(variables: Variables): String =
        operands.joinToString("") { it.getValue(variables) }
}