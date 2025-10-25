// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.value;

/**
 * Immutable representation of MessagePack's String type.
 *
 * @see sirius.stellar.serialization.msgpack.value.StringValue
 * @see sirius.stellar.serialization.msgpack.value.ImmutableRawValue
 */
public interface ImmutableStringValue
        extends StringValue, ImmutableRawValue
{
}
