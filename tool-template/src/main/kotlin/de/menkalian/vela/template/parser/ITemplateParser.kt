package de.menkalian.vela.template.parser

import de.menkalian.vela.template.evaluator.ITemplateEvaluator
import java.io.File
import java.io.InputStream

interface ITemplateParser {
    fun parse(input: String): ITemplateEvaluator
    fun parse(input: InputStream): ITemplateEvaluator = parse(String(input.readAllBytes()))
    fun parse(input: File): ITemplateEvaluator {
        val currentWorkingDir = System.getProperty("user.dir")
        System.setProperty("user.dir", input.parentFile.absolutePath)
        val result = parse(input.readText())
        System.setProperty("user.dir", currentWorkingDir)

        return result
    }

    companion object {
        fun getDefaultImplementation() = getImplementation("vela")

        fun getImplementation(name: String): ITemplateParser =
            when (name) {
                "vela" -> DefaultTemplateParser()
                else   -> throw RuntimeException("Unknown ITemplateParser-Implementation")
            }
    }
}