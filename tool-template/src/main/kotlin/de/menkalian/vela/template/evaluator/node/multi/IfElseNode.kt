package de.menkalian.vela.template.evaluator.node.multi

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.interpretAsBoolean
import de.menkalian.vela.template.evaluator.node.INode
import de.menkalian.vela.template.evaluator.node.leaf.TextNode

class IfElseNode(val condition: INode, val conditionTrueBranch: INode, val conditionFalseBranch: INode = TextNode("")) : IMultiOperatorNode {
    override val operands: List<INode> = listOf(condition, conditionTrueBranch, conditionFalseBranch)

    override fun evaluate(variables: Variables, result: StringBuilder) {
        if (condition.getValue(variables).interpretAsBoolean()) {
            conditionTrueBranch.evaluate(variables, result)
        } else {
            conditionFalseBranch.evaluate(variables, result)
        }
    }

    override fun getValue(variables: Variables): String =
        if (condition.getValue(variables).interpretAsBoolean()) {
            conditionTrueBranch.getValue(variables)
        } else {
            conditionFalseBranch.getValue(variables)
        }
}