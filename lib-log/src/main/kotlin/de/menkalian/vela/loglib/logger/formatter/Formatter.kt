package de.menkalian.vela.loglib.logger.formatter

import de.menkalian.vela.loglib.LoggerFactory
import de.menkalian.vela.loglib.logger.formatter.tags.QuotedStringLogTag
import de.menkalian.vela.loglib.logger.formatter.tags.StackTraceLogTag
import java.util.Locale

class Formatter {
    private val customTags = LoggerFactory.getFactory().configuration.customLogTags + listOf(
        QuotedStringLogTag(),
        StackTraceLogTag(),
    )
    private val completeRegex: Regex = generateFullRegex()
    private val defaultLocale = Locale.getDefault()

    fun format(pattern: String, vararg args: Any) = format(defaultLocale, pattern, *args)

    fun format(locale: Locale?, pattern: String, vararg args: Any): String {
        val argsList = args.toMutableList()
        var argIndex = 0
        var matchResult = completeRegex.find(pattern)
        val replacements = mutableListOf<Pair<IntRange, String>>()

        while (matchResult != null) {
            if (matchResult.value != ESCAPED_PERCENT_REGEX) {
                val logTag = customTags
                    .find { it.tagRegex.toRegex().matches(matchResult!!.value) }
                logTag?.format(locale, matchResult.value, argIndex, argsList)
                    ?.apply {
                        replacements.add(matchResult!!.range to this)
                    }
                argIndex += logTag?.argCount ?: 1
            }

            matchResult = matchResult.next()
        }

        val formattedString = StringBuilder(pattern)
        replacements
            .sortedByDescending { it.first.last }
            .forEach {
                formattedString.replace(it.first.first, it.first.last + 1, it.second)
            }

        return formattedString.toString()
            .format(locale, *(argsList.toTypedArray()))
    }

    private fun generateFullRegex(): Regex {
        val fullList = listOf(ESCAPED_PERCENT_REGEX, FORMAT_CONVERSION_REGEX) + customTags.map { it.tagRegex }
        return fullList.joinToString("|") { "($it)" }.toRegex()
    }

    companion object {
        private const val ESCAPED_PERCENT_REGEX = "%%"
        private const val FORMAT_CONVERSION_REGEX =
            /** ARG_IDX  FLAGS     WIDTH PRECISION  CONVERSION             TIME-FORMATTING                      */
            "%(\\d+\\\$)?[-#+ 0,(]*(\\d*)(\\.\\d*)?([bBhHsScCdoxXeEfgGaAn]|[tT][HIklMSLNpzZsQBbhAaCYyjmdeRTrDFc])"
    }
}