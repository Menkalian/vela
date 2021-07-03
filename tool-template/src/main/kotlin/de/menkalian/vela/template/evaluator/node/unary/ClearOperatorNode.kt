package de.menkalian.vela.template.evaluator.node.unary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.INode

class ClearOperatorNode(override val operand: INode) : IUnaryOperatorNode {
    override fun evaluate(variables: Variables, result: StringBuilder) {
        variables.remove(operand.getValue(variables))
    }
}