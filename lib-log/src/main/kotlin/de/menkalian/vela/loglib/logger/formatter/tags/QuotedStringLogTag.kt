package de.menkalian.vela.loglib.logger.formatter.tags

import de.menkalian.vela.loglib.CustomLogTag
import java.util.Locale

internal class QuotedStringLogTag : CustomLogTag {
    override val tagRegex: String = "%l?q"

    override fun format(locale: Locale?, matchedTag: String, argIndex: Int, args: MutableList<Any>) : String {
        args[argIndex] = "\"${args[argIndex]}\""
        return "%s"
    }
}