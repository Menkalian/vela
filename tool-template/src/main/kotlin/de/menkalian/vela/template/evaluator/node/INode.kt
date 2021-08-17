package de.menkalian.vela.template.evaluator.node

import de.menkalian.vela.template.Variables

interface INode {
    fun evaluate(variables: Variables, result: StringBuilder) {
        result.append(getValue(variables))
    }

    fun getValue(variables: Variables): String {
        throw RuntimeException(this::class.simpleName + " does not return a value.")
    }
}