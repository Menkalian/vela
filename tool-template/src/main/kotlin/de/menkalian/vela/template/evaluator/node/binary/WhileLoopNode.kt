package de.menkalian.vela.template.evaluator.node.binary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.interpretAsBoolean
import de.menkalian.vela.template.evaluator.node.INode

class WhileLoopNode(
    override val leftOperand  /*Condition*/: INode,
    override val rightOperand /*Operation*/: INode
) : IBinaryOperatorNode {
    override fun evaluate(variables: Variables, result: StringBuilder) {
        while (leftOperand.getValue(variables).interpretAsBoolean())
            rightOperand.evaluate(variables, result)
    }
}