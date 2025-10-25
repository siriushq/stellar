// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.value;

/**
 * Immutable base interface of {@link ImmutableIntegerValue} and {@link ImmutableFloatValue} interfaces. To extract primitive type values, call toXXX methods, which may lose some information by rounding or truncation.
 *
 * @see sirius.stellar.serialization.msgpack.value.ImmutableIntegerValue
 * @see sirius.stellar.serialization.msgpack.value.ImmutableFloatValue
 */
public interface ImmutableNumberValue
        extends NumberValue, ImmutableValue
{
}
