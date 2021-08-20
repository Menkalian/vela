package de.menkalian.vela.plain

import java.io.File

class SourceGenerator(val target: File, clazz: String, values: Map<String, String>) {
    init {
        target.parentFile.mkdirs()
        target.delete()
        target.createNewFile()

        val targetPackage = values["group"] ?: "de.menkalian.vela"
        target.appendText("package $targetPackage;\n\n")
        target.appendText(
            "" +
                    "    /**\n" +
                    "     * Generated class (VELA)\n" +
                    "     */\n" +
                    "public class $clazz {\n"
        )

        values.forEach {
            target.appendText(
                "" +
                        "    /**\n" +
                        "     * Generated value (VELA)\n" +
                        "     */\n" +
                        "    public static final String ${it.key} = \"${it.value}\";\n"
            )
        }

        target.appendText("}")
    }
}