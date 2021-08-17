package de.menkalian.vela.template.evaluator.node.unary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.INode

class IsBooleanOperatorNode(override val operand: INode) : IUnaryOperatorNode {
    private val allowedBooleanValues = listOf(
        "true", "false", "0", "1"
    )

    override fun getValue(variables: Variables): String =
        allowedBooleanValues.contains(
            operand.getValue(variables).toLowerCase()
        )
            .toString()
}