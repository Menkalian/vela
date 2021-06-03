package de.menkalian.vela.featuretoggle.compiler

import de.menkalian.vela.featuretoggle.compiler.backend.Generators
import de.menkalian.vela.featuretoggle.compiler.backend.IGenerator
import de.menkalian.vela.featuretoggle.compiler.parser.IParser
import de.menkalian.vela.featuretoggle.compiler.parser.ParserFactory
import de.menkalian.vela.featuretoggle.compiler.transformation.ITreeTransformer
import de.menkalian.vela.featuretoggle.compiler.transformation.TransformerManager
import java.io.File

interface ICompileChain {
    val parser: IParser
    val transformer: ITreeTransformer
    val backend: IGenerator

    fun compile(targetDir: File) {
        val tree = parser.parse()
        transformer.apply(tree)
        backend.generateOutput(tree, targetDir)
    }

    companion object {
        fun create(
            input: File,
            transformer: ITreeTransformer = TransformerManager,
            generator: IGenerator = Generators.KOTLIN
        ) = create(ParserFactory.createParserForFile(input), transformer, generator)

        fun create(
            parser: IParser,
            transformer: ITreeTransformer = TransformerManager,
            generator: IGenerator = Generators.KOTLIN
        ): ICompileChain = DefaultCompileChain(parser, transformer, generator)
    }

    private class DefaultCompileChain(
        override val parser: IParser,
        override val transformer: ITreeTransformer,
        override val backend: IGenerator
    ) : ICompileChain
}

fun main() {
    ICompileChain.create(File("D:\\Projects\\Kotlin\\vela\\lib-featuretoggle\\compiler\\src\\test\\resources\\test_parse.xml")).compile(File("."))
}