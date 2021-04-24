package de.menkalian.vela.transfervalue

internal fun ByteArray.toHexString(): String {
    return joinToString("") { String.format("%02X", it) }
}

internal fun String.parseHex(): ByteArray {
    val toParse = if (length % 2 == 1) {
        "0$this"
    } else {
        this
    }

    return toParse.chunked(2).map { it.toInt(16).toByte() }.toTypedArray().toByteArray()
}
