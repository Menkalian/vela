package de.menkalian.vela.template.evaluator

import de.menkalian.vela.template.Variables

interface ITemplateEvaluator {
    fun evaluate(startVariables: Variables) : String
    fun reset()
}