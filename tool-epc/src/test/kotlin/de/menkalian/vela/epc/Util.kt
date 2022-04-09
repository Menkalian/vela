package de.menkalian.vela.epc

import java.io.File

fun compareFiles(f1: File, f2: File): Boolean {
    println("Comparing $f1 to $f2")
    if (f1.isDirectory) {
        return f1.list()?.let { it.size == f2.list()?.size } == true &&
                f1.list()?.all {
                    compareFiles(File(f1, it), File(f2, it))
                } == true
    } else {
        return f1.exists() && f2.exists() &&
                (f1.readBytes().contentEquals(f2.readBytes())
                        || f1.readText().equalsWithoutLF(f2.readText()))
    }
}

fun String.equalsWithoutLF(other: String) = this.lineFeed().contentEquals(other.lineFeed())

fun String.lineFeed() = this
    .replace("\r\n", "\n")
    .replace("\r", "\n")

