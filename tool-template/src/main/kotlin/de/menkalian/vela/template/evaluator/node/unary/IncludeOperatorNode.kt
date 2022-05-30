package de.menkalian.vela.template.evaluator.node.unary

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.INode
import de.menkalian.vela.template.parser.DefaultTemplateParser
import java.io.File

class IncludeOperatorNode(override val operand: INode) : IUnaryOperatorNode {
    override fun evaluate(variables: Variables, result: StringBuilder) {
        val includeFile = File(System.getProperty("user.dir") + File.separator + operand.getValue(variables))

        return DefaultTemplateParser()
            .parse(includeFile)
            .rootNode
            .evaluate(variables, result)
    }

    override fun getValue(variables: Variables): String {
        val includeFile = File(System.getProperty("user.dir") + File.separator + operand.getValue(variables))

        return DefaultTemplateParser()
            .parse(includeFile)
            .rootNode
            .getValue(variables)
    }
}