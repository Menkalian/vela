package de.menkalian.vela.template.evaluator.node.unary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.GlobalNodeContext.evaluateName
import de.menkalian.vela.template.evaluator.node.INode

class DecrementOperatorNode(override val operand: INode) : IUnaryOperatorNode {
    override fun evaluate(variables: Variables, result: StringBuilder) {
        getValue(variables)
        // Do not output anything
    }

    override fun getValue(variables: Variables): String {
        val varname = evaluateName(operand.getValue(variables), variables)

        variables[varname] = variables
            .getOrDefault(varname, "0")
            .toBigInteger().dec()
            .toString()

        return variables[varname] ?: "0"
    }
}