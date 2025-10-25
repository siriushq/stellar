// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.value;

import java.util.Iterator;
import java.util.List;

/**
 * Immutable representation of MessagePack's Array type.
 *
 * MessagePack's Array type can represent sequence of values.
 */
public interface ImmutableArrayValue
        extends ArrayValue, ImmutableValue
{
    /**
     * Returns an iterator over elements.
     * Returned Iterator does not support {@code remove()} method since the value is immutable.
     */
    Iterator<Value> iterator();

    /**
     * Returns the value as {@code List}.
     * Returned List is immutable. It does not support {@code put()}, {@code clear()}, or other methods that modify the value.
     */
    List<Value> list();
}
