import de.menkalian.vela.template.Variables
import de.menkalian.vela.template.parser.ITemplateParser

class ResourceHelper

fun printExampleResult(path: String) {
    val variables = Variables()
    val input = ResourceHelper::class.java
        .getResourceAsStream(path)!!

    printHeader("PARSER-OUTPUT")
    val evaluated = ITemplateParser
        .getDefaultImplementation()
        .parse(input)
        .evaluate(variables)
    printHeader("VARIABLES")
    println(variables)
    printHeader("RESULT")
    println(evaluated)
}

private fun printHeader(str: String) {
    val width = 120
    val padChar = '*'
    val halfPad = width / 2 + (str.length + 2 /*spaces*/) / 2

    println("$padChar".repeat(120))
    println(" $str ".padStart(halfPad, padChar).padEnd(120, padChar))
    println("$padChar".repeat(120))
}