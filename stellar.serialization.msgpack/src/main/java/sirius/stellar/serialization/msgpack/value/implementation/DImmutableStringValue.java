// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.value.implementation;

import sirius.stellar.serialization.msgpack.MessagePacker;
import sirius.stellar.serialization.msgpack.value.ImmutableStringValue;
import sirius.stellar.serialization.msgpack.value.Value;
import sirius.stellar.serialization.msgpack.value.ValueType;

import java.io.IOException;
import java.util.Arrays;

/**
 * {@code DImmutableStringValue} Implements {@code ImmutableStringValue} using a {@code byte[]} field.
 * This implementation caches result of {@code toString()} and {@code asString()} using a private {@code String} field.
 *
 * @see sirius.stellar.serialization.msgpack.value.StringValue
 */
public class DImmutableStringValue
        extends DAbstractImmutableRawValue
        implements ImmutableStringValue
{
    public DImmutableStringValue(byte[] data)
    {
        super(data);
    }

    public DImmutableStringValue(String string)
    {
        super(string);
    }

    @Override
    public ValueType getValueType()
    {
        return ValueType.STRING;
    }

    @Override
    public ImmutableStringValue immutableValue()
    {
        return this;
    }

    @Override
    public ImmutableStringValue asStringValue()
    {
        return this;
    }

    @Override
    public void writeTo(MessagePacker pk)
            throws IOException
    {
        pk.packRawStringHeader(data.length);
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
        if (!v.isStringValue()) {
            return false;
        }

        if (v instanceof DImmutableStringValue) {
            DImmutableStringValue bv = (DImmutableStringValue) v;
            return Arrays.equals(data, bv.data);
        }
        else {
            return Arrays.equals(data, v.asStringValue().asByteArray());
        }
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(data);
    }
}
