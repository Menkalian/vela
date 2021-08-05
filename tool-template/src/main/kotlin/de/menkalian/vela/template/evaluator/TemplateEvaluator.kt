package de.menkalian.vela.template.evaluator

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.GlobalNodeContext
import de.menkalian.vela.template.evaluator.node.INode

class TemplateEvaluator(private val rootNode: INode) : ITemplateEvaluator {
    private val variables: HashMap<String, String> = hashMapOf()

    override fun evaluate(startVariables: Variables): String {
        val strBuilder = StringBuilder()
        variables.putAll(startVariables)

        // Load Constants
        TemplateConstants.evaluate(variables, StringBuilder())
        rootNode.evaluate(variables, strBuilder)
        return strBuilder.toString()
    }

    override fun reset() {
        variables.clear()
        GlobalNodeContext.clear()
    }
}