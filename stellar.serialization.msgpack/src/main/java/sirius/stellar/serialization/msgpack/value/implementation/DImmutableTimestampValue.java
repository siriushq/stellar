// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.value.implementation;

import sirius.stellar.serialization.msgpack.MessagePacker;
import sirius.stellar.serialization.msgpack.buffer.MessageBuffer;
import sirius.stellar.serialization.msgpack.value.ExtensionValue;
import sirius.stellar.serialization.msgpack.value.ImmutableExtensionValue;
import sirius.stellar.serialization.msgpack.value.ImmutableTimestampValue;
import sirius.stellar.serialization.msgpack.value.TimestampValue;
import sirius.stellar.serialization.msgpack.value.Value;
import sirius.stellar.serialization.msgpack.value.ValueType;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;

import static sirius.stellar.serialization.msgpack.MessagePack.Code.EXT_TIMESTAMP;

/**
 * {@code DImmutableTimestampValue} Implements {@code ImmutableTimestampValue} using a {@code byte} and a {@code byte[]} fields.
 *
 * @see TimestampValue
 */
public class DImmutableTimestampValue
        extends DAbstractImmutableValue
        implements ImmutableExtensionValue, ImmutableTimestampValue
{
    private final Instant instant;
    private byte[] data;

    public DImmutableTimestampValue(Instant timestamp)
    {
        this.instant = timestamp;
    }

    @Override
    public boolean isTimestampValue()
    {
        return true;
    }

    @Override
    public byte getType()
    {
        return EXT_TIMESTAMP;
    }

    @Override
    public ValueType getValueType()
    {
        // Note: Future version should return ValueType.TIMESTAMP instead.
        return ValueType.EXTENSION;
    }

    @Override
    public ImmutableTimestampValue immutableValue()
    {
        return this;
    }

    @Override
    public ImmutableExtensionValue asExtensionValue()
    {
        return this;
    }

    @Override
    public ImmutableTimestampValue asTimestampValue()
    {
        return this;
    }

    @Override
    public byte[] getData()
    {
		if (data != null) return data;

		// See MessagePacker.packTimestampImpl
		byte[] bytes;
		long sec = getEpochSecond();
		int nsec = getNano();
		if (sec >>> 34 == 0) {
			long data64 = ((long) nsec << 34) | sec;
			if ((data64 & 0xffffffff00000000L) == 0L) {
				bytes = new byte[4];
				MessageBuffer.wrap(bytes).putInt(0, (int) sec);
			}
			else {
				bytes = new byte[8];
				MessageBuffer.wrap(bytes).putLong(0, data64);
			}
		}
		else {
			bytes = new byte[12];
			MessageBuffer buffer = MessageBuffer.wrap(bytes);
			buffer.putInt(0, nsec);
			buffer.putLong(4, sec);
		}
		data = bytes;
		return data;
    }

    @Override
    public long getEpochSecond()
    {
        return instant.getEpochSecond();
    }

    @Override
    public int getNano()
    {
        return instant.getNano();
    }

    @Override
    public long toEpochMillis()
    {
        return instant.toEpochMilli();
    }

    @Override
    public Instant toInstant()
    {
        return instant;
    }

    @Override
    public void writeTo(MessagePacker packer)
            throws IOException
    {
        packer.packTimestamp(instant);
    }

    @Override
    public boolean equals(Object o)
    {
        // Implements same behavior with DImmutableExtensionValue.
        if (o == this) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        Value v = (Value) o;

        if (!v.isExtensionValue()) {
            return false;
        }
        ExtensionValue ev = v.asExtensionValue();

        // Here should use isTimestampValue and asTimestampValue instead. However, because
        // adding these methods to Value interface can't keep backward compatibility without
        // using "default" keyword since Java 7, here uses instanceof of and cast instead.
        if (ev instanceof TimestampValue) {
            TimestampValue tv = (TimestampValue) ev;
            return instant.equals(tv.toInstant());
        }
        else {
            return EXT_TIMESTAMP == ev.getType() && Arrays.equals(getData(), ev.getData());
        }
    }

    @Override
    public int hashCode()
    {
        // Implements same behavior with DImmutableExtensionValue.
        int hash = EXT_TIMESTAMP;
        hash *= 31;
        hash = instant.hashCode();
        return hash;
    }

    @Override
    public String toJson()
    {
        return "\"" + toInstant().toString() + "\"";
    }

    @Override
    public String toString()
    {
        return toInstant().toString();
    }
}
