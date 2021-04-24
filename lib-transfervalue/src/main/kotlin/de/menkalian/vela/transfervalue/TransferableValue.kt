package de.menkalian.vela.transfervalue

import de.menkalian.vela.transfervalue.ITransferableValue.TransferableValueType.BIG_INTEGER
import de.menkalian.vela.transfervalue.ITransferableValue.TransferableValueType.BINARY
import de.menkalian.vela.transfervalue.ITransferableValue.TransferableValueType.DOUBLE
import de.menkalian.vela.transfervalue.ITransferableValue.TransferableValueType.INTEGER
import de.menkalian.vela.transfervalue.ITransferableValue.TransferableValueType.LONG
import java.math.BigInteger
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.roundToLong

internal data class TransferableValue(override val type: ITransferableValue.TransferableValueType, override val value: String) : ITransferableValue {
    override fun toString() = value

    override fun toInt(): Int =
        when (type) {
            BINARY      -> value.substring(max(0, value.length - 8)).toInt(16)
            INTEGER     -> value.toInt()
            LONG        -> toLong().toInt()
            DOUBLE      -> toDouble().roundToInt()
            BIG_INTEGER -> toBigInteger().toInt()
            else        -> throw UnsupportedOperationException("Can't interpret $type as Int")
        }

    override fun toLong(): Long =
        when (type) {
            BINARY      -> value.substring(max(0, value.length - 16)).toLong(16)
            INTEGER     -> value.toLong()
            LONG        -> value.toLong()
            DOUBLE      -> toDouble().roundToLong()
            BIG_INTEGER -> toBigInteger().toLong()
            else        -> throw UnsupportedOperationException("Can't interpret $type as Long")
        }

    override fun toDouble(): Double =
        when (type) {
            INTEGER     -> value.toDouble()
            LONG        -> toLong().toDouble()
            DOUBLE      -> value.toDouble()
            BIG_INTEGER -> toBigInteger().toDouble()
            else        -> throw UnsupportedOperationException("Can't interpret $type as Double")
        }

    override fun toByteArray(): ByteArray =
        when (type) {
            BINARY      -> value.parseHex()
            INTEGER     -> (3 downTo 0).map { toInt().shr(8 * it).toByte() }.toByteArray()
            LONG        -> (7 downTo 0).map { toLong().shr(8 * it).toByte() }.toByteArray()
            BIG_INTEGER -> value.parseHex()
            else        -> throw UnsupportedOperationException("Can't interpret $type as binary data")
        }

    override fun toBigInteger(): BigInteger =
        when (type) {
            BINARY      -> BigInteger(toByteArray())
            INTEGER     -> BigInteger.valueOf(toLong())
            LONG        -> BigInteger.valueOf(toLong())
            DOUBLE      -> BigInteger.valueOf(toLong())
            BIG_INTEGER -> BigInteger(toByteArray())
            else        -> throw UnsupportedOperationException("Can't interpret $type as big Integer")
        }
}
