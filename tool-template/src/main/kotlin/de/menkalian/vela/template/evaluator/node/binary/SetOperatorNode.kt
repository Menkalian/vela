package de.menkalian.vela.template.evaluator.node.binary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.GlobalNodeContext.evaluateName
import de.menkalian.vela.template.evaluator.node.INode
import de.menkalian.vela.template.evaluator.node.leaf.TextNode

class SetOperatorNode(override val leftOperand: INode, override val rightOperand: INode) : IBinaryOperatorNode {
    constructor(varName: String, value: String) : this(TextNode(varName), TextNode(value))

    override fun evaluate(variables: Variables, result: StringBuilder) {
        variables[evaluateName(leftOperand.getValue(variables), variables)] =
            rightOperand.getValue(variables)
    }
}