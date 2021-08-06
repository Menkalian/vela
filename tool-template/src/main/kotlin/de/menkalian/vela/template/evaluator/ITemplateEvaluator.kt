package de.menkalian.vela.template.evaluator

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.INode

interface ITemplateEvaluator {
    val rootNode: INode
    fun evaluate(startVariables: Variables = Variables()) : String
    fun reset()
}