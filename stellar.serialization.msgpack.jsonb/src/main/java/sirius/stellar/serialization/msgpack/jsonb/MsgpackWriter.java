package sirius.stellar.serialization.msgpack.jsonb;

import io.avaje.json.JsonIoException;
import io.avaje.json.JsonWriter;
import io.avaje.json.PropertyNames;
import org.jspecify.annotations.Nullable;
import sirius.stellar.serialization.msgpack.MessagePacker;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

/// Implementation of [JsonWriter] for MessagePack.
///
/// When using [JsonWriter#unwrap(Class)], [MessagePacker.class][MessagePacker]
/// should be the provided argument in order to retrieve the correct object.
sealed class MsgpackWriter implements JsonWriter permits MsgpackBufferedWriter, MsgpackBytesWriter {

	protected MessagePacker packer;

	private boolean serializeNulls;
	private boolean serializeEmpty;

	@Nullable
	private MsgpackWriterOperator base;
	@Nullable
	private MsgpackWriterOperator current;

	@Nullable
	private MsgspackPropertyNames allPropertyNames;
	@Nullable
	private String nextPropertyName;

	MsgpackWriter(MessagePacker packer, boolean serializeNulls, boolean serializeEmpty) {
		this.packer = packer;

		this.serializeNulls = serializeNulls;
		this.serializeEmpty = serializeEmpty;
	}

	//#region serialize* accessors
	@Override
	public void serializeNulls(boolean serializeNulls) {
		this.serializeNulls = serializeNulls;
	}

	@Override
	public boolean serializeNulls() {
		return this.serializeNulls;
	}

	@Override
	public void serializeEmpty(boolean serializeEmpty) {
		this.serializeEmpty = serializeEmpty;
	}

	@Override
	public boolean serializeEmpty() {
		return this.serializeEmpty;
	}
	//#endregion

	//#region no-op
	@Override
	public void rawValue(String value) {
		assert true;
	}

	@Override
	public void rawChunkStart() {
		assert true;
	}

	@Override
	public void rawChunk(char value) {
		assert true;
	}

	@Override
	public void rawChunk(String value) {
		assert true;
	}

	@Override
	public void rawChunkEncode(String value) {
		assert true;
	}

	@Override
	public void rawChunkEnd() {
		assert true;
	}

	@Override
	public void writeNewLine() {
		assert true;
	}

	@Override
	public void markIncomplete() {
		assert true;
	}

	@Override
	public void pretty(boolean pretty) {
		assert true;
	}
	//#endregion

	@Override
	public <T> T unwrap(Class<T> underlying) {
		return (T) this.packer;
	}

	@Override
	public String path() {
		return this.packer.toString();
	}

	@Override
	public void name(int i) {
		this.nextPropertyName = this.allPropertyNames.names()[i];
	}

	@Override
	public void name(String nextPropertyName) {
		this.nextPropertyName = nextPropertyName;
	}

	@Override
	public void allNames(PropertyNames names) {
		if (names instanceof MsgspackPropertyNames allPropertyNames) {
			this.allPropertyNames = allPropertyNames;
			return;
		}
		throw new IllegalStateException("MsgpackWriter#allNames(...) called with unsupported PropertyNames instance");
	}

	@Override
	public void beginArray() {
		if (this.base == null) {
			this.base = new MsgpackWriterArrayOperator();
			this.current = this.base;
			return;
		}
		if (this.current == null) {
			this.current = new MsgpackWriterArrayOperator(this.base);
			this.writeNextPropertyName();
			this.base.operation(this.current);
			return;
		}
		MsgpackWriterOperator operator = new MsgpackWriterArrayOperator(this.current);
		this.writeNextPropertyName();
		this.current.operation(operator);
		this.current = operator;
	}

	@Override
	public void endArray() {
		if (this.base == null) throw new UnsupportedOperationException("Attempted to call endArray() when there is no current operation");
		if (this.current == null) throw new UnsupportedOperationException("Attempted to call endArray() when there are no operations left");
		this.current = this.current.parent().orElse(null);
		this.assertAndWrite();
	}

	@Override
	public void emptyArray() {
		try {
			if (this.base == null) {
				this.packer.packArrayHeader(0);
				return;
			}

			if (this.current == null) throw new UnsupportedOperationException("Attempted to call emptyArray() when there are no operations left");
			this.writeNextPropertyName();
			this.current.operation(packer -> packer.packArrayHeader(0));
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public void beginObject() {
		if (this.base == null) {
			this.base = new MsgpackWriterMapOperator();
			this.current = this.base;
			return;
		}
		if (this.current == null) {
			this.current = new MsgpackWriterMapOperator(this.base);
			this.base.operation(this.current);
			return;
		}
		MsgpackWriterOperator operator = new MsgpackWriterMapOperator(this.current);
		this.writeNextPropertyName();
		this.current.operation(operator);
		this.current = operator;
	}

	@Override
	public void beginObject(PropertyNames names) {
		this.allNames(names);
		this.beginObject();
	}

	@Override
	public void endObject() {
		if (this.base == null) throw new UnsupportedOperationException("Attempted to call endArray() when there is no current operation");
		if (this.current == null) throw new UnsupportedOperationException("Attempted to call endArray() when there are no operations left");
		this.current = this.current.parent().orElse(null);
		this.assertAndWrite();
	}

	@Override
	public void nullValue() {
		if (this.base == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there is no current operation");
		if (this.current == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there are no operations left");
		this.writeNextPropertyName();
		this.current.operation(MessagePacker::packNil);
	}

	@Override
	public void value(String value) {
		if (this.base == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there is no current operation");
		if (this.current == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there are no operations left");
		this.writeNextPropertyName();
		this.current.operation(packer -> packer.packString(value));
	}

	@Override
	public void value(boolean value) {
		if (this.base == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there is no current operation");
		if (this.current == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there are no operations left");
		this.writeNextPropertyName();
		this.current.operation(packer -> packer.packBoolean(value));
	}

	@Override
	public void value(int value) {
		if (this.base == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there is no current operation");
		if (this.current == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there are no operations left");
		this.writeNextPropertyName();
		this.current.operation(packer -> packer.packInt(value));
	}

	@Override
	public void value(long value) {
		if (this.base == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there is no current operation");
		if (this.current == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there are no operations left");
		this.writeNextPropertyName();
		this.current.operation(packer -> packer.packLong(value));
	}

	@Override
	public void value(double value) {
		if (this.base == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there is no current operation");
		if (this.current == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there are no operations left");
		this.writeNextPropertyName();
		this.current.operation(packer -> packer.packDouble(value));
	}

	@Override
	public void value(Boolean value) {
		if (this.base == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there is no current operation");
		if (this.current == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there are no operations left");
		this.writeNextPropertyName();
		this.current.operation(packer -> packer.packBoolean(value));
	}

	@Override
	public void value(Integer value) {
		if (this.base == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there is no current operation");
		if (this.current == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there are no operations left");
		this.writeNextPropertyName();
		this.current.operation(packer -> packer.packInt(value));
	}

	@Override
	public void value(Long value) {
		if (this.base == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there is no current operation");
		if (this.current == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there are no operations left");
		this.writeNextPropertyName();
		this.current.operation(packer -> packer.packLong(value));
	}

	@Override
	public void value(Double value) {
		if (this.base == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there is no current operation");
		if (this.current == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there are no operations left");
		this.writeNextPropertyName();
		this.current.operation(packer -> packer.packDouble(value));
	}

	@Override
	public void value(BigDecimal value) {
		this.value(value.toBigIntegerExact());
	}

	@Override
	public void value(BigInteger value) {
		if (this.base == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there is no current operation");
		if (this.current == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there are no operations left");
		this.writeNextPropertyName();
		this.current.operation(packer -> packer.packBigInteger(value));
	}

	@Override
	public void value(byte[] value) {
		if (this.base == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there is no current operation");
		if (this.current == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there are no operations left");
		this.writeNextPropertyName();
		this.current.operation(packer -> {
			packer.packBinaryHeader(value.length);
			packer.writePayload(value);
		});
	}

	@Override
	public void jsonValue(@Nullable Object object) {
		if (object instanceof Map<?, ?> value) {
			value(value);
		} else if (object instanceof Collection<?> value) {
			value(value);
		} else if (object instanceof String value) {
			value(value);
		} else if (object instanceof Boolean value) {
			value(value);
		} else if (object instanceof Integer value) {
			value(value);
		} else if (object instanceof Long value) {
			value(value);
		} else if (object instanceof Double value) {
			value(value);
		} else if (object instanceof BigDecimal value) {
			value(value);
		} else if (object instanceof byte[] value) {
			value(value);
		} else if (object == null) {
			nullValue();
		} else {
			throw new IllegalArgumentException("Unsupported type: " + object.getClass().getName());
		}
	}

	private void value(Map<?, ?> value) {
		if (this.base == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there is no current operation");
		if (this.current == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there are no operations left");
		this.writeNextPropertyName();
		this.current.operation(packer -> {
			packer.packMapHeader(value.size());
			for (Map.Entry<?, ?> entry : value.entrySet()) {
				this.jsonValue(entry.getKey());
				this.jsonValue(entry.getValue());
			}
		});
	}

	private void value(Collection<?> value) {
		if (this.base == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there is no current operation");
		if (this.current == null) throw new UnsupportedOperationException("Attempted to call nullValue() when there are no operations left");
		this.writeNextPropertyName();
		this.current.operation(packer -> {
			packer.packArrayHeader(value.size());
			for (Object object : value) this.jsonValue(object);
		});
	}

	/// Writes the next property name.
	/// This method assumes that [#current] is available.
	private void writeNextPropertyName() {
		if (this.nextPropertyName == null) return;
		String nextPropertyName = this.nextPropertyName;

		assert this.current != null;
		this.current.operation(packer -> packer.packString(nextPropertyName));

		this.nextPropertyName = null;
	}

	/// Writes everything to the stream.
	private void assertAndWrite() {
		if (this.base == null) throw new UnsupportedOperationException("Attempted to call assertAndWrite() with no registered operation");
		if (this.current != null) return;
		this.base.operate(this.packer);
	}

	@Override
	public void flush() {
		try {
			this.packer.flush();
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public void close() {
		try {
			this.packer.close();
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}
}