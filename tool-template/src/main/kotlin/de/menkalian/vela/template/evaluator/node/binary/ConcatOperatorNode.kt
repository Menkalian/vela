package de.menkalian.vela.template.evaluator.node.binary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.INode

class ConcatOperatorNode(override val leftOperand: INode, override val rightOperand: INode) : IBinaryOperatorNode {
    override fun getValue(variables: Variables): String {
        return leftOperand.getValue(variables) + rightOperand.getValue(variables)
    }
}