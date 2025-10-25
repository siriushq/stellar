// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.value;

import java.util.Iterator;
import java.util.List;

/**
 * Representation of MessagePack's Array type.
 *
 * MessagePack's Array type can represent sequence of values.
 */
public interface ArrayValue
        extends Value, Iterable<Value>
{
    /**
     * Returns number of elements in this array.
     */
    int size();

    /**
     * Returns the element at the specified position in this array.
     *
     * @throws IndexOutOfBoundsException If the index is out of range
     * (<code>index &lt; 0 || index &gt;= size()</code>)
     */
    Value get(int index);

    /**
     * Returns the element at the specified position in this array.
     * This method returns an ImmutableNilValue if the index is out of range.
     */
    Value getOrNilValue(int index);

    /**
     * Returns an iterator over elements.
     */
    Iterator<Value> iterator();

    /**
     * Returns the value as {@code List}.
     */
    List<Value> list();
}
