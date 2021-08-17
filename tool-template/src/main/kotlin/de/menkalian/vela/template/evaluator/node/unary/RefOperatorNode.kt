package de.menkalian.vela.template.evaluator.node.unary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.GlobalNodeContext.evaluateName
import de.menkalian.vela.template.evaluator.node.INode

class RefOperatorNode(override val operand: INode) : IUnaryOperatorNode {
    private fun Variables.access(name: String) =
        get(evaluateName(name, this))

    override fun getValue(variables: Variables): String =
        variables.access(variables.access(operand.getValue(variables)) ?: "") ?: ""
}