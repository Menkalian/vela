package de.menkalian.vela.transfervalue

import de.menkalian.vela.TransferableValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TransferableValueTest {

    @Test
    fun fromString() {
        val tVal = TransferableValue.from("Vela is a tested project")
        Assertions.assertEquals(TransferableValue.TransferableValueType.STRING, tVal.type)
        Assertions.assertEquals("Vela is a tested project", tVal.value)
    }

    @Test
    fun fromInt() {
        val tVal = TransferableValue.from(52)
        Assertions.assertEquals(TransferableValue.TransferableValueType.INTEGER, tVal.type)
        Assertions.assertEquals("52", tVal.value)
    }

    @Test
    fun fromLong() {
        val tVal = TransferableValue.from(517L)
        Assertions.assertEquals(TransferableValue.TransferableValueType.LONG, tVal.type)
        Assertions.assertEquals("517", tVal.value)
    }

    @Test
    fun fromDouble() {
        val tVal = TransferableValue.from(-984.147)
        Assertions.assertEquals(TransferableValue.TransferableValueType.DOUBLE, tVal.type)
        Assertions.assertEquals("-984.147", tVal.value)
    }

    @Test
    fun fromByteArray() {
        val tVal = TransferableValue.from(byteArrayOf(9, 14, -3, 127))
        Assertions.assertEquals(TransferableValue.TransferableValueType.BINARY, tVal.type)
        Assertions.assertEquals("090EFD7F", tVal.value)
    }

    @Test
    fun testToString() {
        val strVal = TransferableValue.from("Vela is a tested project")
        val intVal = TransferableValue.from(85)
        val lonVal = TransferableValue.from(517L)
        val douVal = TransferableValue.from(-984.147)
        val binVal = TransferableValue.from(byteArrayOf(9, 14, -3, 127))

        Assertions.assertEquals("Vela is a tested project", strVal.toString())
        Assertions.assertEquals("85", intVal.toString())
        Assertions.assertEquals("517", lonVal.toString())
        Assertions.assertEquals("-984.147", douVal.toString())
        Assertions.assertEquals("090EFD7F", binVal.toString())
    }

    @Test
    fun toInt() {
        val strVal = TransferableValue.from("Vela is a tested project")
        val intVal = TransferableValue.from(85)
        val lonVal = TransferableValue.from(517L)
        val douVal = TransferableValue.from(-984.147)
        val binVal = TransferableValue.from(byteArrayOf(9, 14, -3, 127))

        Assertions.assertThrows(NumberFormatException::class.java) { strVal.toInt() }
        Assertions.assertEquals(85, intVal.toInt())
        Assertions.assertEquals(517, lonVal.toInt())
        Assertions.assertEquals(-984, douVal.toInt())
        Assertions.assertEquals(151_977_343, binVal.toInt())
    }

    @Test
    fun toLong() {
        val strVal = TransferableValue.from("Vela is a tested project")
        val intVal = TransferableValue.from(85)
        val lonVal = TransferableValue.from(517L)
        val douVal = TransferableValue.from(-984.147)
        val binVal = TransferableValue.from(byteArrayOf(9, 14, -3, 127))

        Assertions.assertThrows(NumberFormatException::class.java) { strVal.toLong() }
        Assertions.assertEquals(85L, intVal.toLong())
        Assertions.assertEquals(517L, lonVal.toLong())
        Assertions.assertEquals(-984L, douVal.toLong())
        Assertions.assertEquals(151_977_343L, binVal.toLong())
    }

    @Test
    fun toDouble() {
        val strVal = TransferableValue.from("Vela is a tested project")
        val intVal = TransferableValue.from(85)
        val lonVal = TransferableValue.from(517L)
        val douVal = TransferableValue.from(-984.147)
        val binVal = TransferableValue.from(byteArrayOf(9, 14, -3, 127))

        Assertions.assertThrows(NumberFormatException::class.java) { strVal.toDouble() }
        Assertions.assertEquals(85.0, intVal.toDouble())
        Assertions.assertEquals(517.0, lonVal.toDouble())
        Assertions.assertEquals(-984.147, douVal.toDouble())
        Assertions.assertDoesNotThrow { binVal.toDouble() }
    }

    @Test
    fun toByteArray() {
        val strVal = TransferableValue.from("Vela is a tested project")
        val intVal = TransferableValue.from(85)
        val lonVal = TransferableValue.from(517L)
        val douVal = TransferableValue.from(-984.147)
        val binVal = TransferableValue.from(byteArrayOf(9, 14, -3, 127))

        Assertions.assertArrayEquals("Vela is a tested project".toByteArray(Charsets.UTF_8), strVal.toByteArray())
        Assertions.assertTrue(byteArrayOf(0, 0, 0, 85).contentEquals(intVal.toByteArray()), "Content was ${intVal.toByteArray().toList()}")
        Assertions.assertTrue(byteArrayOf(0, 0, 0, 0, 0, 0, 2, 5).contentEquals(lonVal.toByteArray()), "Content was ${lonVal.toByteArray().toList()}")
        Assertions.assertDoesNotThrow { douVal.toByteArray() }
        Assertions.assertTrue(byteArrayOf(9, 14, -3, 127).contentEquals(binVal.toByteArray()), "Content was ${binVal.toByteArray().toList()}")
    }
}