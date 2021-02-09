package de.menkalian.vela.epc

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals

internal class ByteArrayUtilsKtTest {

    @org.junit.jupiter.api.Test
    fun toHexString() {
        val byteArray = byteArrayOf(1, 6, 127, 0, 14, -8, -128)
        assertEquals("01067f000ef880", byteArray.toHexString())
    }

    @org.junit.jupiter.api.Test
    fun parseHexBytes() {
        val expected = byteArrayOf(1, 2, 127, 14, 69)
        assertArrayEquals(expected, "01027f0e45".parseHexBytes())
    }
}