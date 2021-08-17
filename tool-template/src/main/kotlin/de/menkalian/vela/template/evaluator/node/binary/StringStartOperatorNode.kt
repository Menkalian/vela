package de.menkalian.vela.template.evaluator.node.binary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.INode

class StringStartOperatorNode(override val leftOperand: INode, override val rightOperand: INode) : IBinaryOperatorNode {
    override fun getValue(variables: Variables): String {
        val leftOp = leftOperand.getValue(variables)
        val rightOp = rightOperand.getValue(variables)
        return leftOp.substring(0, Math.min(rightOp.toInt(), leftOp.length))
    }
}