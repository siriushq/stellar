// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.value;

import sirius.stellar.serialization.msgpack.MessageFormat;
import sirius.stellar.serialization.msgpack.exception.MessageIntegerOverflowException;

import java.math.BigInteger;

/**
 * Representation of MessagePack's Integer type.
 *
 * MessagePack's Integer type can represent from -2<sup>63</sup> to 2<sup>64</sup>-1.
 */
public interface IntegerValue
        extends NumberValue
{
    /**
     * Returns true if the value is in the range of [-2<sup>7</sup> to 2<sup>7</sup>-1].
     */
    boolean isInByteRange();

    /**
     * Returns true if the value is in the range of [-2<sup>15</sup> to 2<sup>15</sup>-1]
     */
    boolean isInShortRange();

    /**
     * Returns true if the value is in the range of [-2<sup>31</sup> to 2<sup>31</sup>-1]
     */
    boolean isInIntRange();

    /**
     * Returns true if the value is in the range of [-2<sup>63</sup> to 2<sup>63</sup>-1]
     */
    boolean isInLongRange();

    /**
     * Returns the most succinct MessageFormat type to represent this integer value.
     *
     * @return the smallest integer type of MessageFormat that is big enough to store the value.
     */
    MessageFormat mostSuccinctMessageFormat();

    /**
     * Returns the value as a {@code byte}, otherwise throws an exception.
     *
     * @throws MessageIntegerOverflowException If the value does not fit in the range of {@code byte} type.
     */
    byte asByte();

    /**
     * Returns the value as a {@code short}, otherwise throws an exception.
     *
     * @throws MessageIntegerOverflowException If the value does not fit in the range of {@code short} type.
     */
    short asShort();

    /**
     * Returns the value as an {@code int}, otherwise throws an exception.
     *
     * @throws MessageIntegerOverflowException If the value does not fit in the range of {@code int} type.
     */
    int asInt();

    /**
     * Returns the value as a {@code long}, otherwise throws an exception.
     *
     * @throws MessageIntegerOverflowException If the value does not fit in the range of {@code long} type.
     */
    long asLong();

    /**
     * Returns the value as a {@code BigInteger}.
     */
    BigInteger asBigInteger();
}
