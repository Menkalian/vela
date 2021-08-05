package de.menkalian.vela.template.evaluator.node.unary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.GlobalNodeContext
import de.menkalian.vela.template.evaluator.node.INode

class StringLengthOperatorNode(override val operand: INode) : IUnaryOperatorNode {
    override fun getValue(variables: Variables): String =
        operand.getValue(variables).length.toString()
}