package de.menkalian.vela.template.evaluator.node.binary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.GlobalNodeContext.evaluateName
import de.menkalian.vela.template.evaluator.node.INode
import de.menkalian.vela.template.evaluator.node.leaf.TextNode

class StringStartOperatorNode(override val leftOperand: INode, override val rightOperand: INode) : IBinaryOperatorNode {
    override fun getValue(variables: Variables): String =
        leftOperand.getValue(variables).substring(0, rightOperand.getValue(variables).toInt())
}