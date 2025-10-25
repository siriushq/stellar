// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.value;

/**
 * Representation of MessagePack's Float type.
 *
 * MessagePack's Float type can represent IEEE 754 double precision floating point numbers including NaN and infinity. This is same with Java's {@code double} type.
 *
 * @see sirius.stellar.serialization.msgpack.value.NumberValue
 */
public interface FloatValue
        extends NumberValue
{
}
