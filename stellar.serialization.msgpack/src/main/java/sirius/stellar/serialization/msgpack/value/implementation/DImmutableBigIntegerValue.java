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
 * {@code DImmutableBigIntegerValue} Implements {@code ImmutableBigIntegerValue} using a {@code BigInteger} field.
 *
 * @see sirius.stellar.serialization.msgpack.value.IntegerValue
 */
public class DImmutableBigIntegerValue
        extends DAbstractImmutableValue
        implements ImmutableIntegerValue
{
    public static MessageFormat mostSuccinctMessageFormat(IntegerValue v)
    {
        if (v.isInByteRange()) {
            return MessageFormat.INT8;
        }
        else if (v.isInShortRange()) {
            return MessageFormat.INT16;
        }
        else if (v.isInIntRange()) {
            return MessageFormat.INT32;
        }
        else if (v.isInLongRange()) {
            return MessageFormat.INT64;
        }
        else {
            return MessageFormat.UINT64;
        }
    }

    private final BigInteger value;

    public DImmutableBigIntegerValue(BigInteger value)
    {
        this.value = value;
    }

    private static final BigInteger BYTE_MIN = BigInteger.valueOf((long) Byte.MIN_VALUE);
    private static final BigInteger BYTE_MAX = BigInteger.valueOf((long) Byte.MAX_VALUE);
    private static final BigInteger SHORT_MIN = BigInteger.valueOf((long) Short.MIN_VALUE);
    private static final BigInteger SHORT_MAX = BigInteger.valueOf((long) Short.MAX_VALUE);
    private static final BigInteger INT_MIN = BigInteger.valueOf((long) Integer.MIN_VALUE);
    private static final BigInteger INT_MAX = BigInteger.valueOf((long) Integer.MAX_VALUE);
    private static final BigInteger LONG_MIN = BigInteger.valueOf((long) Long.MIN_VALUE);
    private static final BigInteger LONG_MAX = BigInteger.valueOf((long) Long.MAX_VALUE);

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
        return value.byteValue();
    }

    @Override
    public short toShort()
    {
        return value.shortValue();
    }

    @Override
    public int toInt()
    {
        return value.intValue();
    }

    @Override
    public long toLong()
    {
        return value.longValue();
    }

    @Override
    public BigInteger toBigInteger()
    {
        return value;
    }

    @Override
    public float toFloat()
    {
        return value.floatValue();
    }

    @Override
    public double toDouble()
    {
        return value.doubleValue();
    }

    @Override
    public boolean isInByteRange()
    {
        return 0 <= value.compareTo(BYTE_MIN) && value.compareTo(BYTE_MAX) <= 0;
    }

    @Override
    public boolean isInShortRange()
    {
        return 0 <= value.compareTo(SHORT_MIN) && value.compareTo(SHORT_MAX) <= 0;
    }

    @Override
    public boolean isInIntRange()
    {
        return 0 <= value.compareTo(INT_MIN) && value.compareTo(INT_MAX) <= 0;
    }

    @Override
    public boolean isInLongRange()
    {
        return 0 <= value.compareTo(LONG_MIN) && value.compareTo(LONG_MAX) <= 0;
    }

    @Override
    public MessageFormat mostSuccinctMessageFormat()
    {
        return mostSuccinctMessageFormat(this);
    }

    @Override
    public byte asByte()
    {
        if (!isInByteRange()) {
            throw new MessageIntegerOverflowException(value);
        }
        return value.byteValue();
    }

    @Override
    public short asShort()
    {
        if (!isInShortRange()) {
            throw new MessageIntegerOverflowException(value);
        }
        return value.shortValue();
    }

    @Override
    public int asInt()
    {
        if (!isInIntRange()) {
            throw new MessageIntegerOverflowException(value);
        }
        return value.intValue();
    }

    @Override
    public long asLong()
    {
        if (!isInLongRange()) {
            throw new MessageIntegerOverflowException(value);
        }
        return value.longValue();
    }

    @Override
    public BigInteger asBigInteger()
    {
        return value;
    }

    @Override
    public void writeTo(MessagePacker pk)
            throws IOException
    {
        pk.packBigInteger(value);
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
        return value.equals(iv.toBigInteger());
    }

    @Override
    public int hashCode()
    {
        if (INT_MIN.compareTo(value) <= 0 && value.compareTo(INT_MAX) <= 0) {
            return (int) value.longValue();
        }
        else if (LONG_MIN.compareTo(value) <= 0
                && value.compareTo(LONG_MAX) <= 0) {
            long v = value.longValue();
            return (int) (v ^ (v >>> 32));
        }
        return value.hashCode();
    }

    @Override
    public String toJson()
    {
        return value.toString();
    }

    @Override
    public String toString()
    {
        return toJson();
    }
}
