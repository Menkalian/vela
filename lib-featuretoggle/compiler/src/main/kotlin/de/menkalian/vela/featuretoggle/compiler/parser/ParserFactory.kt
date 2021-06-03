package de.menkalian.vela.featuretoggle.compiler.parser

import de.menkalian.vela.featuretoggle.compiler.parser.xml.XmlParser
import java.io.File

object ParserFactory {
    private val registeredParserCompanions = mutableListOf<IParser.IParserCompanion>()

    init {
        // Register the parsers
        registerParser(XmlParser)
    }

    /**
     * Registers an new parser companion to be used when looking for an parser
     */
    fun registerParser(companion: IParser.IParserCompanion) {
        registeredParserCompanions.add(companion)
    }

    /**
     * Provides the default IParser implementation for the given file.
     * @throws NoParserImplementationException If no valid implementation is found
     */
    fun createParserForFile(file: File): IParser {
        return registeredParserCompanions
            .firstOrNull {
                it.supportedFileExtensions.contains(file.extension.toLowerCase())
            }
            ?.createInstance(file)
            ?: throw NoParserImplementationException(file.extension)
    }
}
