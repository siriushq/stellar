// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.value;

/**
 * Representation MessagePack's Boolean type.
 *
 * MessagePack's Boolean type can represent {@code true} or {@code false}.
 */
public interface BooleanValue
        extends Value
{
    /**
     * Returns the value as a {@code boolean}.
     */
    boolean getBoolean();
}
