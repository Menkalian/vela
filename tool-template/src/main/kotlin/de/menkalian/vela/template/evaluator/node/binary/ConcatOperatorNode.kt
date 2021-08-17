package de.menkalian.vela.template.evaluator.node.binary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.GlobalNodeContext
import de.menkalian.vela.template.evaluator.node.INode

class ConcatOperatorNode(override val leftOperand: INode, override val rightOperand: INode) : IBinaryOperatorNode {
    override fun evaluate(variables: Variables, result: StringBuilder) {
        val evaluatedName = GlobalNodeContext.evaluateName(leftOperand.getValue(variables), variables)
        variables[evaluatedName] += rightOperand.getValue(variables)
        result.append(variables[evaluatedName])
    }
}