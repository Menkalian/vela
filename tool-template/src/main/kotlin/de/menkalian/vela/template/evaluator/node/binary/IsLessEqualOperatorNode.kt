package de.menkalian.vela.template.evaluator.node.binary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.INode

class IsLessEqualOperatorNode(override val leftOperand: INode, override val rightOperand: INode) : IBinaryOperatorNode {
    override fun getValue(variables: Variables): String {
        val leftValue = leftOperand.getValue(variables)
        val rightValue = rightOperand.getValue(variables)

        // Try as integers, else compare strings
        val leftInt = leftValue.toBigIntegerOrNull()
        val rightInt = rightValue.toBigIntegerOrNull()
        val result = if (leftInt != null && rightInt != null) {
            leftInt.compareTo(rightInt)
        } else {
            leftValue.compareTo(rightValue)
        }

        return (result <= 0).toString()
    }
}