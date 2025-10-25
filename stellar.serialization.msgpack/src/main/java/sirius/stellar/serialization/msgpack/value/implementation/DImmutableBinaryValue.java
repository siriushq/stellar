// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.value.implementation;

import sirius.stellar.serialization.msgpack.MessagePacker;
import sirius.stellar.serialization.msgpack.value.ImmutableBinaryValue;
import sirius.stellar.serialization.msgpack.value.Value;
import sirius.stellar.serialization.msgpack.value.ValueType;

import java.io.IOException;
import java.util.Arrays;

/**
 * {@code DImmutableBinaryValue} Implements {@code ImmutableBinaryValue} using a {@code byte[]} field.
 * This implementation caches result of {@code toString()} and {@code asString()} using a private {@code String} field.
 *
 * @see sirius.stellar.serialization.msgpack.value.StringValue
 */
public class DImmutableBinaryValue
        extends DAbstractImmutableRawValue
        implements ImmutableBinaryValue
{
    public DImmutableBinaryValue(byte[] data)
    {
        super(data);
    }

    @Override
    public ValueType getValueType()
    {
        return ValueType.BINARY;
    }

    @Override
    public ImmutableBinaryValue immutableValue()
    {
        return this;
    }

    @Override
    public ImmutableBinaryValue asBinaryValue()
    {
        return this;
    }

    @Override
    public void writeTo(MessagePacker pk)
            throws IOException
    {
        pk.packBinaryHeader(data.length);
        pk.writePayload(data);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        Value v = (Value) o;
        if (!v.isBinaryValue()) {
            return false;
        }

        if (v instanceof DImmutableBinaryValue) {
            DImmutableBinaryValue bv = (DImmutableBinaryValue) v;
            return Arrays.equals(data, bv.data);
        }
        else {
            return Arrays.equals(data, v.asBinaryValue().asByteArray());
        }
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(data);
    }
}
