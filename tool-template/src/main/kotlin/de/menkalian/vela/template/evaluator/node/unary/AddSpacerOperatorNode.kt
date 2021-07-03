package de.menkalian.vela.template.evaluator.node.unary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.GlobalNodeContext
import de.menkalian.vela.template.evaluator.node.INode

class AddSpacerOperatorNode(override val operand: INode) : IUnaryOperatorNode {
    override fun evaluate(variables: Variables, result: StringBuilder) {
        GlobalNodeContext.separators.add(operand.getValue(variables))
    }
}