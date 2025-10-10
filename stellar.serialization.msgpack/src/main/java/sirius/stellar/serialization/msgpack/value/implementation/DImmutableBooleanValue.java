package sirius.stellar.serialization.msgpack.value.implementation;

import sirius.stellar.serialization.msgpack.MessagePacker;
import sirius.stellar.serialization.msgpack.value.ImmutableBooleanValue;
import sirius.stellar.serialization.msgpack.value.Value;
import sirius.stellar.serialization.msgpack.value.ValueType;

import java.io.IOException;

/**
 * {@code DImmutableBooleanValue} Implements {@code ImmutableBooleanValue} using a {@code boolean} field.
 *
 * This class is a singleton. {@code DImmutableBooleanValue.trueInstance()} and {@code DImmutableBooleanValue.falseInstance()} are the only instances of this class.
 *
 * @see sirius.stellar.serialization.msgpack.value.BooleanValue
 */
public class DImmutableBooleanValue
        extends DAbstractImmutableValue
        implements ImmutableBooleanValue
{
    public static final ImmutableBooleanValue TRUE = new DImmutableBooleanValue(true);
    public static final ImmutableBooleanValue FALSE = new DImmutableBooleanValue(false);

    private final boolean value;

    private DImmutableBooleanValue(boolean value)
    {
        this.value = value;
    }

    @Override
    public ValueType getValueType()
    {
        return ValueType.BOOLEAN;
    }

    @Override
    public ImmutableBooleanValue asBooleanValue()
    {
        return this;
    }

    @Override
    public ImmutableBooleanValue immutableValue()
    {
        return this;
    }

    @Override
    public boolean getBoolean()
    {
        return value;
    }

    @Override
    public void writeTo(MessagePacker packer)
            throws IOException
    {
        packer.packBoolean(value);
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

        if (!v.isBooleanValue()) {
            return false;
        }
        return value == v.asBooleanValue().getBoolean();
    }

    @Override
    public int hashCode()
    {
        if (value) {
            return 1231;
        }
        else {
            return 1237;
        }
    }

    @Override
    public String toJson()
    {
        return Boolean.toString(value);
    }

    @Override
    public String toString()
    {
        return toJson();
    }
}
