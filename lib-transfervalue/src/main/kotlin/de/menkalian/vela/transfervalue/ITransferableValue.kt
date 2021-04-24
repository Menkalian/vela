package de.menkalian.vela.transfervalue

import java.math.BigInteger

/**
 * The public datatype this library exposes.
 * To construct one of these use the Methods of the companion object
 */
interface ITransferableValue {
    val type: TransferableValueType
    val value: String

    // Conversion Methods
    override fun toString(): String
    fun toInt(): Int
    fun toLong(): Long
    fun toDouble(): Double
    fun toByteArray(): ByteArray
    fun toBigInteger(): BigInteger

    companion object {
        fun from(str: String): ITransferableValue = TransferableValue(TransferableValueType.STRING, str)
        fun from(i: Int): ITransferableValue = TransferableValue(TransferableValueType.INTEGER, i.toString())
        fun from(l: Long): ITransferableValue = TransferableValue(TransferableValueType.LONG, l.toString())
        fun from(d: Double): ITransferableValue = TransferableValue(TransferableValueType.DOUBLE, d.toString())
        fun from(bytes: ByteArray): ITransferableValue = TransferableValue(TransferableValueType.BINARY, bytes.toHexString())
        fun from(bigint: BigInteger): ITransferableValue = TransferableValue(TransferableValueType.BIG_INTEGER, bigint.toByteArray().toHexString())
    }

    enum class TransferableValueType {
        STRING,
        BINARY,
        INTEGER,
        LONG,
        DOUBLE,
        BIG_INTEGER
    }
}
