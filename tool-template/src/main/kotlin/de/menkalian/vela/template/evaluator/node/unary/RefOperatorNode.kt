package de.menkalian.vela.template.evaluator.node.unary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.GlobalNodeContext.evaluateName
import de.menkalian.vela.template.evaluator.node.INode

class RefOperatorNode(override val operand: INode) : IUnaryOperatorNode {
    override fun getValue(variables: Variables): String = variables[
            evaluateName(operand.getValue(variables), variables)
    ] ?: ""
}