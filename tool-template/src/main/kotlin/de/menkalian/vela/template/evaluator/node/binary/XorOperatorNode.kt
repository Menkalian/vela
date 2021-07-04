package de.menkalian.vela.template.evaluator.node.binary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.interpretAsBoolean
import de.menkalian.vela.template.evaluator.node.INode

class XorOperatorNode(override val leftOperand: INode, override val rightOperand: INode) : IBinaryOperatorNode {
    override fun getValue(variables: Variables): String {
        return (leftOperand.getValue(variables).interpretAsBoolean() xor
                rightOperand.getValue(variables).interpretAsBoolean()).toString()
    }
}