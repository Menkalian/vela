package de.menkalian.vela.template.evaluator.node.unary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.INode

class PlaceholderNode(override val operand: INode) : IUnaryOperatorNode {
    override fun getValue(variables: Variables): String = operand.getValue(variables)
}