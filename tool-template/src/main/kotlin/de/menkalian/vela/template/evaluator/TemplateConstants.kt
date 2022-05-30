package de.menkalian.vela.template.evaluator

import de.menkalian.vela.BuildConfig
import de.menkalian.vela.template.evaluator.node.binary.SetOperatorNode
import de.menkalian.vela.template.evaluator.node.multi.ConcatenationNode

object TemplateConstants : ConcatenationNode(
    listOf(
        // BOOLS
        SetOperatorNode("Const.True", "true"),
        SetOperatorNode("Const.False", "false"),

        // NUMBERS
        SetOperatorNode("Const.Zero", "0"),
        SetOperatorNode("Const.One", "1"),
        SetOperatorNode("Const.Two", "2"),
        SetOperatorNode("Const.Ten", "10"),

        // TEXT
        SetOperatorNode("Engine.Version", BuildConfig.version),
        SetOperatorNode("Engine.Language.Version", "1.0"),
        SetOperatorNode("Engine.Name", BuildConfig.group),
        SetOperatorNode("Engine.Hint", "##### Created with Vela Engine (${BuildConfig.group}) V${BuildConfig.version} #####"),
    )
)