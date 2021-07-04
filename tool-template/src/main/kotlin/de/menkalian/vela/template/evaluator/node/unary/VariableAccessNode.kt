package de.menkalian.vela.template.evaluator.node.unary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.GlobalNodeContext.evaluateName
import de.menkalian.vela.template.evaluator.node.INode
import de.menkalian.vela.template.evaluator.node.leaf.TextNode

class VariableAccessNode(override val operand: INode) : IUnaryOperatorNode {
    constructor(text: String) : this(TextNode(text))

    override fun getValue(variables: Variables): String = variables[evaluateName(operand.getValue(variables), variables)] ?: ""
}