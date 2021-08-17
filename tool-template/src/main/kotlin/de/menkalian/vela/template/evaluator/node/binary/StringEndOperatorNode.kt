package de.menkalian.vela.template.evaluator.node.binary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.INode

class StringEndOperatorNode(override val leftOperand: INode, override val rightOperand: INode) : IBinaryOperatorNode {
    override fun getValue(variables: Variables): String {
        val str = leftOperand.getValue(variables)
        return str.substring((str.length - rightOperand.getValue(variables).toInt()).coerceAtLeast(0))
    }
}