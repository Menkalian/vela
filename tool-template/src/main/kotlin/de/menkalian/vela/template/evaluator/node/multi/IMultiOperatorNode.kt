package de.menkalian.vela.template.evaluator.node.multi

import de.menkalian.vela.template.evaluator.node.INode

interface IMultiOperatorNode : INode {
    val operands: List<INode>
}