package sirius.stellar.serialization.msgpack.value.implementation;

import sirius.stellar.serialization.msgpack.MessagePacker;
import sirius.stellar.serialization.msgpack.value.ImmutableNilValue;
import sirius.stellar.serialization.msgpack.value.Value;
import sirius.stellar.serialization.msgpack.value.ValueType;

import java.io.IOException;

/**
 * {@code DImmutableNilValue} Implements {@code ImmutableNilValue}.
 *
 * This class is a singleton. {@code DImmutableNilValue.get()} is the only instances of this class.
 *
 * @see sirius.stellar.serialization.msgpack.value.NilValue
 */
public class DImmutableNilValue
        extends DAbstractImmutableValue
        implements ImmutableNilValue
{
    private static ImmutableNilValue instance = new DImmutableNilValue();

    public static ImmutableNilValue get()
    {
        return instance;
    }

    private DImmutableNilValue()
    {
    }

    @Override
    public ValueType getValueType()
    {
        return ValueType.NIL;
    }

    @Override
    public ImmutableNilValue immutableValue()
    {
        return this;
    }

    @Override
    public ImmutableNilValue asNilValue()
    {
        return this;
    }

    @Override
    public void writeTo(MessagePacker pk)
            throws IOException
    {
        pk.packNil();
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
        return ((Value) o).isNilValue();
    }

    @Override
    public int hashCode()
    {
        return 0;
    }

    @Override
    public String toString()
    {
        return toJson();
    }

    @Override
    public String toJson()
    {
        return "null";
    }
}
