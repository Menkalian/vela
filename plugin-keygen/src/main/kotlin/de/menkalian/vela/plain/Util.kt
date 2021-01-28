package de.menkalian.vela.plain

import java.io.File

fun transformToYaml(input: File): String {
    val transformed = StringBuilder()

    val lines = input.readLines()
    lines.forEach { line ->
        if (!line.contains(':') && line.isNotBlank())
            transformed.append("$line:\n")
        else
            transformed.append("$line\n")
    }
    return transformed.toString()
}
