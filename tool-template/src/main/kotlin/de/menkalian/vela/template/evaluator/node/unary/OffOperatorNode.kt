package de.menkalian.vela.template.evaluator.node.unary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.INode

class OffOperatorNode(override val operand: INode) : IUnaryOperatorNode {
    override fun evaluate(variables: Variables, result: StringBuilder) {
        operand.evaluate(variables, StringBuilder())
    }

    override fun getValue(variables: Variables): String = ""
}