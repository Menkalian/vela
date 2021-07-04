package de.menkalian.vela.template.evaluator.node.multi

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.GlobalNodeContext.evaluateName
import de.menkalian.vela.template.evaluator.node.INode

class ForLoopNode(val varname: INode, val bottomValue: INode, val topValue: INode, val operation: INode) : IMultiOperatorNode {
    override val operands: List<INode> = listOf(varname, bottomValue, topValue, operation)

    override fun evaluate(variables: Variables, result: StringBuilder) {
        val evalVarname = evaluateName(varname.getValue(variables), variables)
        val origValue = variables[evalVarname]

        val bottomValueInt = bottomValue.getValue(variables).toBigInteger()
        val topValueInt = topValue.getValue(variables).toBigInteger()

        var currentInt = bottomValueInt

        while (currentInt <= topValueInt) {
            variables[evalVarname] = currentInt.toString()
            operation.evaluate(variables, result)
            currentInt++
        }

        if (origValue != null) {
            variables[evalVarname] = origValue
        } else {
            variables.remove(evalVarname)
        }
    }
}