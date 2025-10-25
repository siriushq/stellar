// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.value.implementation;

import sirius.stellar.serialization.msgpack.MessageFormat;
import sirius.stellar.serialization.msgpack.exception.MessageIntegerOverflowException;
import sirius.stellar.serialization.msgpack.MessagePacker;
import sirius.stellar.serialization.msgpack.value.ImmutableIntegerValue;
import sirius.stellar.serialization.msgpack.value.ImmutableNumberValue;
import sirius.stellar.serialization.msgpack.value.IntegerValue;
import sirius.stellar.serialization.msgpack.value.Value;
import sirius.stellar.serialization.msgpack.value.ValueType;

import java.io.IOException;
import java.math.BigInteger;

/**
 * {@code DImmutableLongValue} Implements {@code ImmutableIntegerValue} using a {@code long} field.
 *
 * @see sirius.stellar.serialization.msgpack.value.IntegerValue
 */
public class DImmutableLongValue
        extends DAbstractImmutableValue
        implements ImmutableIntegerValue
{
    private final long value;

    public DImmutableLongValue(long value)
    {
        this.value = value;
    }

    private static final long BYTE_MIN = (long) Byte.MIN_VALUE;
    private static final long BYTE_MAX = (long) Byte.MAX_VALUE;
    private static final long SHORT_MIN = (long) Short.MIN_VALUE;
    private static final long SHORT_MAX = (long) Short.MAX_VALUE;
    private static final long INT_MIN = (long) Integer.MIN_VALUE;
    private static final long INT_MAX = (long) Integer.MAX_VALUE;

    @Override
    public ValueType getValueType()
    {
        return ValueType.INTEGER;
    }

    @Override
    public ImmutableIntegerValue immutableValue()
    {
        return this;
    }

    @Override
    public ImmutableNumberValue asNumberValue()
    {
        return this;
    }

    @Override
    public ImmutableIntegerValue asIntegerValue()
    {
        return this;
    }

    @Override
    public byte toByte()
    {
        return (byte) value;
    }

    @Override
    public short toShort()
    {
        return (short) value;
    }

    @Override
    public int toInt()
    {
        return (int) value;
    }

    @Override
    public long toLong()
    {
        return value;
    }

    @Override
    public BigInteger toBigInteger()
    {
        return BigInteger.valueOf(value);
    }

    @Override
    public float toFloat()
    {
        return (float) value;
    }

    @Override
    public double toDouble()
    {
        return (double) value;
    }

    @Override
    public boolean isInByteRange()
    {
        return BYTE_MIN <= value && value <= BYTE_MAX;
    }

    @Override
    public boolean isInShortRange()
    {
        return SHORT_MIN <= value && value <= SHORT_MAX;
    }

    @Override
    public boolean isInIntRange()
    {
        return INT_MIN <= value && value <= INT_MAX;
    }

    @Override
    public boolean isInLongRange()
    {
        return true;
    }

    @Override
    public MessageFormat mostSuccinctMessageFormat()
    {
        return DImmutableBigIntegerValue.mostSuccinctMessageFormat(this);
    }

    @Override
    public byte asByte()
    {
        if (!isInByteRange()) {
            throw new MessageIntegerOverflowException(value);
        }
        return (byte) value;
    }

    @Override
    public short asShort()
    {
        if (!isInShortRange()) {
            throw new MessageIntegerOverflowException(value);
        }
        return (short) value;
    }

    @Override
    public int asInt()
    {
        if (!isInIntRange()) {
            throw new MessageIntegerOverflowException(value);
        }
        return (int) value;
    }

    @Override
    public long asLong()
    {
        return value;
    }

    @Override
    public BigInteger asBigInteger()
    {
        return BigInteger.valueOf((long) value);
    }

    @Override
    public void writeTo(MessagePacker pk)
            throws IOException
    {
        pk.packLong(value);
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        Value v = (Value) o;
        if (!v.isIntegerValue()) {
            return false;
        }

        IntegerValue iv = v.asIntegerValue();
        if (!iv.isInLongRange()) {
            return false;
        }
        return value == iv.toLong();
    }

    @Override
    public int hashCode()
    {
        if (INT_MIN <= value && value <= INT_MAX) {
            return (int) value;
        }
        else {
            return (int) (value ^ (value >>> 32));
        }
    }

    @Override
    public String toJson()
    {
        return Long.toString(value);
    }

    @Override
    public String toString()
    {
        return toJson();
    }
}
