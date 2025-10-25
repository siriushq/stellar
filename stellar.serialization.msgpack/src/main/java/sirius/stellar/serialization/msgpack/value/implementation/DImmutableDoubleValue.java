// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.value.implementation;

import sirius.stellar.serialization.msgpack.MessagePacker;
import sirius.stellar.serialization.msgpack.value.ImmutableNumberValue;
import sirius.stellar.serialization.msgpack.value.ImmutableFloatValue;
import sirius.stellar.serialization.msgpack.value.Value;
import sirius.stellar.serialization.msgpack.value.ValueType;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * {@code DImmutableDoubleValue} Implements {@code ImmutableFloatValue} using a {@code double} field.
 *
 * @see sirius.stellar.serialization.msgpack.value.FloatValue
 */
public class DImmutableDoubleValue
        extends DAbstractImmutableValue
        implements ImmutableFloatValue
{
    private final double value;

    public DImmutableDoubleValue(double value)
    {
        this.value = value;
    }

    @Override
    public ValueType getValueType()
    {
        return ValueType.FLOAT;
    }

    @Override
    public DImmutableDoubleValue immutableValue()
    {
        return this;
    }

    @Override
    public ImmutableNumberValue asNumberValue()
    {
        return this;
    }

    @Override
    public ImmutableFloatValue asFloatValue()
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
        return (long) value;
    }

    @Override
    public BigInteger toBigInteger()
    {
        return new BigDecimal(value).toBigInteger();
    }

    @Override
    public float toFloat()
    {
        return (float) value;
    }

    @Override
    public double toDouble()
    {
        return value;
    }

    @Override
    public void writeTo(MessagePacker pk)
            throws IOException
    {
        pk.packDouble(value);
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

        if (!v.isFloatValue()) {
            return false;
        }
        return value == v.asFloatValue().toDouble();
    }

    @Override
    public int hashCode()
    {
        long v = Double.doubleToLongBits(value);
        return (int) (v ^ (v >>> 32));
    }

    @Override
    public String toJson()
    {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "null";
        }
        else {
            return Double.toString(value);
        }
    }

    @Override
    public String toString()
    {
        return Double.toString(value);
    }
}
