package de.menkalian.vela.plain

open class TemplateLoader {
    protected val replacements: HashMap<String, String> = HashMap()

    fun setReplacement(replacement: Pair<String, String>) {
        replacements[replacement.first] = replacement.second
    }

    fun loadTemplate(templateId: String, indentationLevel: Int = 0): String {
        var toReturn = String(javaClass.classLoader.getResourceAsStream("de/menkalian/vela/plain/$templateId.template")!!.readBytes())

        replacements.forEach {
            toReturn = toReturn.replace(it.key, it.value)
        }

        toReturn = toReturn.lines().joinToString("\n") {
            "    ".repeat(indentationLevel) + it
        }

        return toReturn
    }
}