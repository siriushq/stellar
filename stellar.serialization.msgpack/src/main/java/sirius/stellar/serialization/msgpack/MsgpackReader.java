package sirius.stellar.serialization.msgpack;

import io.avaje.json.JsonIoException;
import io.avaje.json.JsonReader;
import io.avaje.json.PropertyNames;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.ValueType;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Implementation of {@link JsonReader} for MessagePack.
 */
final class MsgpackReader implements JsonReader {

	private final MessageUnpacker unpacker;
	private final boolean failOnUnknown;

	private Token current;
	private int currentLength;

	MsgpackReader(MessageUnpacker unpacker, boolean failOnUnknown) {
		this.unpacker = unpacker;
		this.failOnUnknown = failOnUnknown;

		this.current = Token.BEGIN_OBJECT;
		this.currentLength = Integer.MIN_VALUE;
	}

	@Override
	public <T> T unwrap(Class<T> clazz) {
		return (T) this.unpacker;
	}

	@Override
	public void beginArray() {
		try {
			ValueType next = this.unpacker.getNextFormat().getValueType();
			if (!next.isArrayType()) throw new JsonIoException("Expected array but got " + next);

			this.current = Token.BEGIN_ARRAY;
			this.currentLength = this.unpacker.unpackArrayHeader();
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public void endArray() {
		this.currentLength = Integer.MIN_VALUE;
	}

	@Override
	public boolean hasNextElement() {
		try {
			return this.currentLength > 0 && this.unpacker.hasNext();
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public void beginObject(PropertyNames names) {
		beginObject();
	}

	@Override
	public void beginObject() {
		try {
			ValueType next = this.unpacker.getNextFormat().getValueType();
			if (!next.isMapType()) throw new JsonIoException("Expected map but got " + next);

			this.current = Token.BEGIN_OBJECT;
			this.currentLength = this.unpacker.unpackMapHeader();
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public void endObject() {
		this.currentLength = Integer.MIN_VALUE;
	}

	@Override
	public boolean hasNextField() {
		try {
			return this.currentLength > 0 && this.unpacker.hasNext();
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public String nextField() {
		try {
			this.currentLength--;
			this.current = Token.STRING;
			return this.unpacker.unpackValue().asStringValue().asString();
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public boolean isNullValue() {
		try {
			return this.unpacker.getNextFormat() == MessageFormat.NIL;
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public boolean readBoolean() {
		try {
			this.current = Token.BOOLEAN;
			return this.unpacker.unpackBoolean();
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public int readInt() {
		try {
			this.current = Token.NUMBER;
			return this.unpacker.unpackInt();
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public long readLong() {
		try {
			this.current = Token.NUMBER;
			return this.unpacker.unpackLong();
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public double readDouble() {
		try {
			this.current = Token.NUMBER;
			return this.unpacker.unpackDouble();
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public BigDecimal readDecimal() {
		try {
			this.current = Token.NUMBER;
			return new BigDecimal(this.unpacker.unpackBigInteger());
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public BigInteger readBigInteger() {
		try {
			this.current = Token.NUMBER;
			return this.unpacker.unpackBigInteger();
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public String readString() {
		try {
			this.current = Token.STRING;
			return this.unpacker.unpackString();
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public byte[] readBinary() {
		try {
			this.current = Token.STRING;
			int length = this.unpacker.unpackBinaryHeader();
			return this.unpacker.readPayload(length);
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public String readRaw() {
		try {
			this.current = Token.STRING;
			int length = this.unpacker.unpackRawStringHeader();
			return new String(this.unpacker.readPayload(length));
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public String location() {
		return Long.toString(this.unpacker.getTotalReadBytes());
	}

	@Override
	public Token currentToken() {
		return this.current;
	}

	@Override
	public void close() {
		try {
			this.unpacker.close();
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public void skipValue() {
		try {
			this.unpacker.skipValue();
		} catch (IOException exception) {
			throw new JsonIoException(exception);
		}
	}

	@Override
	public void unmappedField(String fieldName) {
		if (!this.failOnUnknown) return;
		throw new IllegalStateException("Unknown property '" + fieldName + "' at " + this.unpacker.getTotalReadBytes());
	}
}