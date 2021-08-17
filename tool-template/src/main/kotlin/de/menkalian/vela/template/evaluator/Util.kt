package de.menkalian.vela.template.evaluator

fun String.interpretAsBoolean() =
    this.equals("false", true).not() && this != "0" && this.isNotBlank()