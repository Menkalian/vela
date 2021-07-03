package de.menkalian.vela.template.evaluator.node.unary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.INode

class IsNumericOperatorNode(override val operand: INode) : IUnaryOperatorNode {
    override fun getValue(variables: Variables): String {
        return (operand.getValue(variables).toBigIntegerOrNull() != null).toString()
    }
}