package de.menkalian.vela.template.evaluator.node

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.unary.VariableAccessNode

object GlobalNodeContext {
    val separators = mutableListOf<String>()
    val dynamicReplacementEntries = mutableListOf<ReplacementEntry>()

    init {
        clear()
    }

    fun evaluateName(varname: String, variables: Variables): String {
        var working = varname
        for (s in separators) {
            working = working
                .split(s)
                .map {
                    dynamicReplacementEntries
                        .firstOrNull { entry -> entry.key == it }
                        ?.calculate(variables) ?: it
                }
                .joinToString(s)
        }
        return working
    }

    fun addDynamicReplacementEntry(key: String, accessor: INode, format: String) {
        dynamicReplacementEntries.add(ReplacementEntry(key, accessor, format))
    }

    fun clear() {
        separators.clear()
        separators.add(".")

        dynamicReplacementEntries.clear()
        addDynamicReplacementEntry("XXX", VariableAccessNode("X"), "%03d")
        addDynamicReplacementEntry("YYY", VariableAccessNode("Y"), "%03d")
        addDynamicReplacementEntry("ZZZ", VariableAccessNode("Z"), "%03d")
    }

    data class ReplacementEntry(val key: String, val accessorNode: INode, val format: String) {
        fun calculate(variables: Variables): String {
            val string = accessorNode.getValue(variables)
            if (string.isBlank()) {
                return key
            }

            val fomatNumberIndicators = listOf("d", "x", "X", "l")
            return if (fomatNumberIndicators.any { format.endsWith(it) })
                String.format(format, string.toLong())
            else
                String.format(format, string)
        }
    }
}