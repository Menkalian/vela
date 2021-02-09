package de.menkalian.vela.epc

import java.io.File
import java.util.Arrays

fun compareFiles(f1: File, f2: File): Boolean {
    if (f1.isDirectory) {
        return f1.list()?.size == f2.list()?.size ?: false &&
                f1.list()?.all {
                    compareFiles(File(f1, it), File(f2, it))
                } ?: false

    } else {
        return f1.exists() && f2.exists() &&
                f1.length() == f2.length() &&
                Arrays.equals(f1.readBytes(), f2.readBytes())
    }
}