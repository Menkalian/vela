package de.menkalian.vela.gradle

fun transformToYaml(input: String): String {
    val transformed = StringBuilder()

    val lines = input.lines()
    lines.forEach { line ->
        if (!line.contains(':') && line.isNotBlank() && !line.startsWith("---"))
            transformed.append("$line:\n")
        else
            transformed.append("$line\n")
    }
    return transformed.toString()
}
