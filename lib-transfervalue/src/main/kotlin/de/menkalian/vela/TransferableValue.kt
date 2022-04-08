package de.menkalian.vela

import de.menkalian.vela.TransferableValue.TransferableValueType.BIG_INTEGER
import de.menkalian.vela.TransferableValue.TransferableValueType.BINARY
import de.menkalian.vela.TransferableValue.TransferableValueType.DOUBLE
import de.menkalian.vela.TransferableValue.TransferableValueType.INTEGER
import de.menkalian.vela.TransferableValue.TransferableValueType.LONG
import de.menkalian.vela.TransferableValue.TransferableValueType.STRING
import java.math.BigInteger
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * Class to encapsulate basic data types as `String`, so they can be stored in the same data structure (e.g. `Map<String, TransferableValue>`).
 * This class is also serializable with the `kotlinx.serialization` library.
 *
 * @constructor Creates an instance from the given value. It is **not** recommended using the constructor directly. You should use the various `from` methods instead.
 */
@kotlinx.serialization.Serializable
data class TransferableValue(val type: TransferableValueType, val value: String) {
    companion object {
        /**
         * Creates an instance with type `STRING`
         */
        fun from(str: String) = TransferableValue(STRING, str)

        /**
         * Creates an instance with type `TransferableValue.INTEGER`
         */
        fun from(i: Int) = TransferableValue(INTEGER, i.toString())

        /**
         * Creates an instance with type `TransferableValue.LONG`
         */
        fun from(l: Long) = TransferableValue(LONG, l.toString())

        /**
         * Creates an instance with type `TransferableValue.DOUBLE`
         */
        fun from(d: Double) = TransferableValue(DOUBLE, d.toString())

        /**
         * Creates an instance with type `TransferableValue.BINARY`
         */
        fun from(bytes: ByteArray) = TransferableValue(BINARY, bytes.toHexString())

        /**
         * Creates an instance with type `TransferableValue.BIG_INTEGER`
         */
        fun from(bigint: BigInteger) = TransferableValue(BIG_INTEGER, bigint.toByteArray().toHexString())

        /**
         * Represents the content of an `ByteArray` as a hexadecimal String
         */
        private fun ByteArray.toHexString(): String {
            return joinToString("") { String.format("%02X", it) }
        }

        /**
         * Converts this (hexadecimal) String to an array of bytes.
         */
        private fun String.parseHex(): ByteArray {
            val toParse = if (length % 2 == 1) {
                "0$this"
            } else {
                this
            }

            return toParse.chunked(2).map { it.toInt(16).toByte() }.toTypedArray().toByteArray()
        }
    }

    /**
     * Returns the String value of this object.
     * This is identical to the representation of the value which is stored.
     */
    override fun toString() = value

    /**
     * Tries to interpret this value as Integer and returns it.
     *
     * @throws UnsupportedOperationException if the value can not be converted to an Integer
     */
    fun toInt(): Int =
        when (type) {
            BINARY      -> value.substring(max(0, value.length - 8)).toInt(16)
            INTEGER     -> value.toInt()
            LONG        -> toLong().toInt()
            DOUBLE      -> toDouble().roundToInt()
            BIG_INTEGER -> toBigInteger().toInt()
            else        -> throw UnsupportedOperationException("Can't interpret $type as Int")
        }

    /**
     * Tries to interpret this value as Long and returns it.
     *
     * @throws UnsupportedOperationException if the value can not be converted to a Long
     */
    fun toLong(): Long =
        when (type) {
            BINARY      -> value.substring(max(0, value.length - 16)).toLong(16)
            INTEGER     -> value.toLong()
            LONG        -> value.toLong()
            DOUBLE      -> toDouble().roundToLong()
            BIG_INTEGER -> toBigInteger().toLong()
            else        -> throw UnsupportedOperationException("Can't interpret $type as Long")
        }

    /**
     * Tries to interpret this value as Double and returns it.
     *
     * @throws UnsupportedOperationException if the value can not be converted to a Double
     */
    fun toDouble(): Double =
        when (type) {
            INTEGER     -> value.toDouble()
            LONG        -> toLong().toDouble()
            DOUBLE      -> value.toDouble()
            BIG_INTEGER -> toBigInteger().toDouble()
            else        -> throw UnsupportedOperationException("Can't interpret $type as Double")
        }

    /**
     * Returns the binary representation of this value.
     *
     * @throws UnsupportedOperationException if there is no binary representation
     */
    fun toByteArray(): ByteArray =
        when (type) {
            BINARY      -> value.parseHex()
            INTEGER     -> (3 downTo 0).map { toInt().shr(8 * it).toByte() }.toByteArray()
            LONG        -> (7 downTo 0).map { toLong().shr(8 * it).toByte() }.toByteArray()
            BIG_INTEGER -> value.parseHex()
            else        -> throw UnsupportedOperationException("Can't interpret $type as binary data")
        }

    /**
     * Tries to interpret this value as BigInteger and returns it.
     *
     * @throws UnsupportedOperationException if the value can not be converted to a BigInteger
     */
    fun toBigInteger(): BigInteger =
        when (type) {
            BINARY      -> BigInteger(toByteArray())
            INTEGER     -> BigInteger.valueOf(toLong())
            LONG        -> BigInteger.valueOf(toLong())
            DOUBLE      -> BigInteger.valueOf(toLong())
            BIG_INTEGER -> BigInteger(toByteArray())
            else        -> throw UnsupportedOperationException("Can't interpret $type as big Integer")
        }

    /**
     * Defines all possible types which can be represented in an object
     *
     * @property STRING Plain String/Text. The saved representation is identical to the value
     * @property BINARY Raw/Binary data. It is saved as hexadecimal string.
     * @property INTEGER 32-bit Integer. Saved as decimal string.
     * @property LONG 64-bit Integer. Saved as decimal string.
     * @property DOUBLE 64-bit floating point. Saved as decimal string.
     * @property BIG_INTEGER unlimited size
     */
    enum class TransferableValueType {
        STRING,
        BINARY,
        INTEGER,
        LONG,
        DOUBLE,
        BIG_INTEGER
    }
}
