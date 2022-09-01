package de.menkalian.vela.loglib

import java.util.Locale

/**
 * Interface for a custom Formatter-Tag, usable in Log-Messages.
 *
 * The following Formatter-Tags are already provided by the library:
 *  - "%q"/"%lq": Inserts the `String`-Value of the argument, surrounded by quotes
 *  - "%lx": Inserts the Message and StackTrace of the argument, if it is a `Throwable`
 */
interface CustomLogTag {
    /**
     * Regex to match a placeholder handled by this LogTag.
     * You may use placeholders of any form, but they must not collide with existing LogTags.
     * The library uses the prefix "%l" for provided LogTags, but the prefix "%ll" is reserved for Placeholders implemented by users of the library.
     */
    val tagRegex: String

    /**
     * Amount of arguments this LogTag uses from the argument list.
     * The given amount will be skipped after invoking this LogTag.
     */
    val argCount: Int
        get() = 1

    /**
     * Generates the replacement string for `matchedTag`.
     *
     * @param locale Optional `Locale` to use for formatting.
     * @param matchedTag Matched placeholder. Advanced LogTags may process args in their placeholders.
     * @param argIndex Index of the first argument associated with this LogTag
     * @param args Input Arguments of the format-call
     *
     * @return Replacement to put in the formatted string.
     */
    fun format(locale: Locale?, matchedTag: String, argIndex: Int, args: MutableList<Any>): String
}