package de.menkalian.vela.loglib.logger.formatter.tags

import de.menkalian.vela.loglib.CustomLogTag
import java.util.Locale

internal class StackTraceLogTag : CustomLogTag {
    override val tagRegex: String = "%lx" // l = prefix "log", x = "eXeption"

    override fun format(locale: Locale?, matchedTag: String, argIndex: Int, args: MutableList<Any>): String {
        val t = args.get(argIndex)
        if (t is Throwable) {
            args[argIndex] = "${t.javaClass.canonicalName}: ${t.message}\n${t.stackTraceToString()}"
        } else {
            args[argIndex] = "Non throwable argument: \"${t}\""
        }
        return "%s"
    }
}