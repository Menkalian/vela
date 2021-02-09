package de.menkalian.vela.epc

internal fun ByteArray.toHexString(): String =
    map { String.format("%02x", it) }.reduce { full, part -> full + part }

internal fun String.parseHexBytes(): ByteArray =
    chunked(2).map { it.toInt(16).toByte() }.toTypedArray().toByteArray()
