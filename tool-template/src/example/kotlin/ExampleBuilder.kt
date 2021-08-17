import de.menkalian.vela.template.parser.ITemplateParser
import java.io.File
import java.io.PrintStream

class ResourceHelper

fun printExampleResult(path: String, printOutputToFile: Boolean = false) {
    enableOutput(path, printOutputToFile)
    val input = ResourceHelper::class.java
        .getResourceAsStream(path)!!

    if (!printOutputToFile)
        printHeader("PARSER-OUTPUT")
    val evaluated = ITemplateParser
        .getDefaultImplementation()
        .parse(input)
        .evaluate()
    if (!printOutputToFile)
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

private fun enableOutput(path: String, enable: Boolean) {
    if (enable) {
        val input = File(path)
        val output = File("output/${input.nameWithoutExtension}")
        output.parentFile.mkdirs()
        System.setOut(PrintStream(output))
    }
}