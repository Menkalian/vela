package de.menkalian.vela.template.evaluator.node.unary

import de.menkalian.vela.template.evaluator.node.INode

interface IUnaryOperatorNode : INode {
    val operand: INode
}