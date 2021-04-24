package de.menkalian.vela.transfervalue

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigInteger

internal class TransferableValueTest {

    @Test
    fun fromString() {
        val tVal = ITransferableValue.from("Vela is a tested project")
        Assertions.assertEquals(ITransferableValue.TransferableValueType.STRING, tVal.type)
        Assertions.assertEquals("Vela is a tested project", tVal.value)
    }

    @Test
    fun fromInt() {
        val tVal = ITransferableValue.from(52)
        Assertions.assertEquals(ITransferableValue.TransferableValueType.INTEGER, tVal.type)
        Assertions.assertEquals("52", tVal.value)
    }

    @Test
    fun fromLong() {
        val tVal = ITransferableValue.from(517L)
        Assertions.assertEquals(ITransferableValue.TransferableValueType.LONG, tVal.type)
        Assertions.assertEquals("517", tVal.value)
    }

    @Test
    fun fromDouble() {
        val tVal = ITransferableValue.from(-984.147)
        Assertions.assertEquals(ITransferableValue.TransferableValueType.DOUBLE, tVal.type)
        Assertions.assertEquals("-984.147", tVal.value)
    }

    @Test
    fun fromByteArray() {
        val tVal = ITransferableValue.from(byteArrayOf(9, 14, -3, 127))
        Assertions.assertEquals(ITransferableValue.TransferableValueType.BINARY, tVal.type)
        Assertions.assertEquals("090EFD7F", tVal.value)
    }

    @Test
    fun fromBigInteger() {
        val tVal = ITransferableValue.from(BigInteger.valueOf(267709L))
        Assertions.assertEquals(ITransferableValue.TransferableValueType.BIG_INTEGER, tVal.type)
        Assertions.assertEquals(BigInteger.valueOf(267709L), BigInteger(tVal.value.parseHex()))
    }

    @Test
    fun testToString() {
        val strVal = ITransferableValue.from("Vela is a tested project")
        val intVal = ITransferableValue.from(85)
        val lonVal = ITransferableValue.from(517L)
        val douVal = ITransferableValue.from(-984.147)
        val binVal = ITransferableValue.from(byteArrayOf(9, 14, -3, 127))
        val bigVal = ITransferableValue.from(BigInteger.valueOf(267709L))

        Assertions.assertEquals("Vela is a tested project", strVal.toString())
        Assertions.assertEquals("85", intVal.toString())
        Assertions.assertEquals("517", lonVal.toString())
        Assertions.assertEquals("-984.147", douVal.toString())
        Assertions.assertEquals("090EFD7F", binVal.toString())
        Assertions.assertEquals("0415BD", bigVal.toString())
    }

    @Test
    fun toInt() {
        val strVal = ITransferableValue.from("Vela is a tested project")
        val intVal = ITransferableValue.from(85)
        val lonVal = ITransferableValue.from(517L)
        val douVal = ITransferableValue.from(-984.147)
        val binVal = ITransferableValue.from(byteArrayOf(9, 14, -3, 127))
        val bigVal = ITransferableValue.from(BigInteger.valueOf(267709L))

        Assertions.assertThrows(UnsupportedOperationException::class.java) { strVal.toInt() }
        Assertions.assertEquals(85, intVal.toInt())
        Assertions.assertEquals(517, lonVal.toInt())
        Assertions.assertEquals(-984, douVal.toInt())
        Assertions.assertEquals(151_977_343, binVal.toInt())
        Assertions.assertEquals(267709, bigVal.toInt())
    }

    @Test
    fun toLong() {
        val strVal = ITransferableValue.from("Vela is a tested project")
        val intVal = ITransferableValue.from(85)
        val lonVal = ITransferableValue.from(517L)
        val douVal = ITransferableValue.from(-984.147)
        val binVal = ITransferableValue.from(byteArrayOf(9, 14, -3, 127))
        val bigVal = ITransferableValue.from(BigInteger.valueOf(267709L))

        Assertions.assertThrows(UnsupportedOperationException::class.java) { strVal.toLong() }
        Assertions.assertEquals(85L, intVal.toLong())
        Assertions.assertEquals(517L, lonVal.toLong())
        Assertions.assertEquals(-984L, douVal.toLong())
        Assertions.assertEquals(151_977_343L, binVal.toLong())
        Assertions.assertEquals(267709L, bigVal.toLong())
    }

    @Test
    fun toDouble() {
        val strVal = ITransferableValue.from("Vela is a tested project")
        val intVal = ITransferableValue.from(85)
        val lonVal = ITransferableValue.from(517L)
        val douVal = ITransferableValue.from(-984.147)
        val binVal = ITransferableValue.from(byteArrayOf(9, 14, -3, 127))
        val bigVal = ITransferableValue.from(BigInteger.valueOf(267709L))

        Assertions.assertThrows(UnsupportedOperationException::class.java) { strVal.toDouble() }
        Assertions.assertEquals(85.0, intVal.toDouble())
        Assertions.assertEquals(517.0, lonVal.toDouble())
        Assertions.assertEquals(-984.147, douVal.toDouble())
        Assertions.assertThrows(UnsupportedOperationException::class.java) { binVal.toDouble() }
        Assertions.assertEquals(267709.0, bigVal.toDouble())
    }

    @Test
    fun toByteArray() {
        val strVal = ITransferableValue.from("Vela is a tested project")
        val intVal = ITransferableValue.from(85)
        val lonVal = ITransferableValue.from(517L)
        val douVal = ITransferableValue.from(-984.147)
        val binVal = ITransferableValue.from(byteArrayOf(9, 14, -3, 127))
        val bigVal = ITransferableValue.from(BigInteger.valueOf(267709L))

        Assertions.assertThrows(UnsupportedOperationException::class.java) { strVal.toByteArray() }
        Assertions.assertTrue(byteArrayOf(0, 0, 0, 85).contentEquals(intVal.toByteArray()), "Content was ${intVal.toByteArray().toList()}")
        Assertions.assertTrue(byteArrayOf(0, 0, 0, 0, 0, 0, 2, 5).contentEquals(lonVal.toByteArray()), "Content was ${lonVal.toByteArray().toList()}")
        Assertions.assertThrows(UnsupportedOperationException::class.java) { douVal.toByteArray() }
        Assertions.assertTrue(byteArrayOf(9, 14, -3, 127).contentEquals(binVal.toByteArray()), "Content was ${binVal.toByteArray().toList()}")
        Assertions.assertTrue(byteArrayOf(4, 21, -67).contentEquals(bigVal.toByteArray()), "Content was ${bigVal.toByteArray().toList()}")
    }

    @Test
    fun toBigInteger() {
        val strVal = ITransferableValue.from("Vela is a tested project")
        val intVal = ITransferableValue.from(85)
        val lonVal = ITransferableValue.from(517L)
        val douVal = ITransferableValue.from(-984.147)
        val binVal = ITransferableValue.from(byteArrayOf(9, 14, -3, 127))
        val bigVal = ITransferableValue.from(BigInteger.valueOf(267709L))

        Assertions.assertThrows(UnsupportedOperationException::class.java) { strVal.toBigInteger() }
        Assertions.assertEquals(BigInteger.valueOf(85), intVal.toBigInteger())
        Assertions.assertEquals(BigInteger.valueOf(517), lonVal.toBigInteger())
        Assertions.assertEquals(BigInteger.valueOf(-984), douVal.toBigInteger())
        Assertions.assertDoesNotThrow { binVal.toBigInteger() }
        Assertions.assertEquals(BigInteger.valueOf(267709L), bigVal.toBigInteger())
    }
}