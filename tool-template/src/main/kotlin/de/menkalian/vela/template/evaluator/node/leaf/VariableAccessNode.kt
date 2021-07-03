package de.menkalian.vela.template.evaluator.node.leaf

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.GlobalNodeContext.evaluateName

class VariableAccessNode(val varname: String) : ILeafNode {
    override fun getValue(variables: Variables): String = variables[evaluateName(varname, variables)] ?: ""
}