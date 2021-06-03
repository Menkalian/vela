package de.menkalian.vela.featuretoggle.compiler.parser

import de.menkalian.vela.featuretoggle.compiler.tree.IFeatureTree
import java.io.File
import java.io.InputStream

interface IParser {
    fun parse(): IFeatureTree

    interface IParserCompanion {
        val supportedFileExtensions: List<String>

        fun createInstance(file: File): IParser {
            val inStream = file.inputStream()
            val toReturn = createInstance(inStream)
            inStream.close()
            return toReturn
        }

        fun createInstance(inputStream: InputStream): IParser
    }
}