// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.value;

import java.math.BigInteger;

/**
 * Base interface of {@link IntegerValue} and {@link FloatValue} interfaces. To extract primitive type values, call toXXX methods, which may lose some information by rounding or truncation.
 *
 * @see sirius.stellar.serialization.msgpack.value.IntegerValue
 * @see sirius.stellar.serialization.msgpack.value.FloatValue
 */
public interface NumberValue
        extends Value
{
    /**
     * Represent this value as a byte value, which may involve rounding or truncation of the original value.
     * the value.
     */
    byte toByte();

    /**
     * Represent this value as a short value, which may involve rounding or truncation of the original value.
     */
    short toShort();

    /**
     * Represent this value as an int value, which may involve rounding or truncation of the original value.
     * value.
     */
    int toInt();

    /**
     * Represent this value as a long value, which may involve rounding or truncation of the original value.
     */
    long toLong();

    /**
     * Represent this value as a BigInteger, which may involve rounding or truncation of the original value.
     */
    BigInteger toBigInteger();

    /**
     * Represent this value as a 32-bit float value, which may involve rounding or truncation of the original value.
     */
    float toFloat();

    /**
     * Represent this value as a 64-bit double value, which may involve rounding or truncation of the original value.
     */
    double toDouble();
}
