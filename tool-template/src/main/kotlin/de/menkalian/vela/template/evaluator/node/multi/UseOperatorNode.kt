package de.menkalian.vela.template.evaluator.node.multi

import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.evaluator.node.GlobalNodeContext
import de.menkalian.vela.template.evaluator.node.INode

class UseOperatorNode(val keyNode: INode, val accessorNode: INode, val formatNode: INode) : IMultiOperatorNode {
    override val operands: List<INode> = listOf(keyNode, accessorNode, formatNode)

    override fun evaluate(variables: Variables, result: StringBuilder) {
        val key = keyNode.getValue(variables)
        val format = formatNode.getValue(variables)

        GlobalNodeContext.addDynamicReplacementEntry(key, accessorNode, format)
    }
}