package de.menkalian.vela.template.evaluator.node.binary

import de.menkalian.vela.template.evaluator.node.INode

interface IBinaryOperatorNode : INode {
    val leftOperand: INode
    val rightOperand: INode
}