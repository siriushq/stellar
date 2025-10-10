package sirius.stellar.serialization.msgpack.value;

import sirius.stellar.serialization.msgpack.value.implementation.*;
import sirius.stellar.serialization.msgpack.value.implementation.DImmutableBinaryValue;

import java.math.BigInteger;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ValueFactory {

	private ValueFactory() {
	}

	public static ImmutableNilValue newNil() {
		return DImmutableNilValue.get();
	}

	public static ImmutableBooleanValue newBoolean(boolean v) {
		return v ? DImmutableBooleanValue.TRUE : DImmutableBooleanValue.FALSE;
	}

	public static ImmutableIntegerValue newInteger(byte v) {
		return new DImmutableLongValue(v);
	}

	public static ImmutableIntegerValue newInteger(short v) {
		return new DImmutableLongValue(v);
	}

	public static ImmutableIntegerValue newInteger(int v) {
		return new DImmutableLongValue(v);
	}

	public static ImmutableIntegerValue newInteger(long v) {
		return new DImmutableLongValue(v);
	}

	public static ImmutableIntegerValue newInteger(BigInteger v) {
		return new DImmutableBigIntegerValue(v);
	}

	public static ImmutableFloatValue newFloat(float v) {
		return new DImmutableDoubleValue(v);
	}

	public static ImmutableFloatValue newFloat(double v) {
		return new DImmutableDoubleValue(v);
	}

	public static ImmutableBinaryValue newBinary(byte[] b) {
		return newBinary(b, false);
	}

	public static ImmutableBinaryValue newBinary(byte[] b, boolean omitCopy) {
		if (omitCopy) {
			return new DImmutableBinaryValue(b);
		} else {
			return new DImmutableBinaryValue(Arrays.copyOf(b, b.length));
		}
	}

	public static ImmutableBinaryValue newBinary(byte[] b, int off, int len) {
		return newBinary(b, off, len, false);
	}

	public static ImmutableBinaryValue newBinary(byte[] b, int off, int len, boolean omitCopy) {
		if (omitCopy && off == 0 && len == b.length) {
			return new DImmutableBinaryValue(b);
		} else {
			return new DImmutableBinaryValue(Arrays.copyOfRange(b, off, len));
		}
	}

	public static ImmutableStringValue newString(String s) {
		return new DImmutableStringValue(s);
	}

	public static ImmutableStringValue newString(byte[] b) {
		return new DImmutableStringValue(b);
	}

	public static ImmutableStringValue newString(byte[] b, boolean omitCopy) {
		if (omitCopy) {
			return new DImmutableStringValue(b);
		} else {
			return new DImmutableStringValue(Arrays.copyOf(b, b.length));
		}
	}

	public static ImmutableStringValue newString(byte[] b, int off, int len) {
		return newString(b, off, len, false);
	}

	public static ImmutableStringValue newString(byte[] b, int off, int len, boolean omitCopy) {
		if (omitCopy && off == 0 && len == b.length) {
			return new DImmutableStringValue(b);
		} else {
			return new DImmutableStringValue(Arrays.copyOfRange(b, off, len));
		}
	}

	public static ImmutableArrayValue newArray(List<? extends Value> list) {
		if (list.isEmpty()) {
			return DImmutableArrayValue.empty();
		}
		Value[] array = list.toArray(new Value[list.size()]);
		return new DImmutableArrayValue(array);
	}

	public static ImmutableArrayValue newArray(Value... array) {
		if (array.length == 0) {
			return DImmutableArrayValue.empty();
		} else {
			return new DImmutableArrayValue(Arrays.copyOf(array, array.length));
		}
	}

	public static ImmutableArrayValue newArray(Value[] array, boolean omitCopy) {
		if (array.length == 0) {
			return DImmutableArrayValue.empty();
		} else if (omitCopy) {
			return new DImmutableArrayValue(array);
		} else {
			return new DImmutableArrayValue(Arrays.copyOf(array, array.length));
		}
	}

	public static ImmutableArrayValue emptyArray() {
		return DImmutableArrayValue.empty();
	}

	public static <K extends Value, V extends Value> ImmutableMapValue newMap(Map<K, V> map) {
		Value[] kvs = new Value[map.size() * 2];
		int index = 0;
		for (Map.Entry<K, V> pair : map.entrySet()) {
			kvs[index] = pair.getKey();
			index++;
			kvs[index] = pair.getValue();
			index++;
		}
		return new DImmutableMapValue(kvs);
	}

	public static ImmutableMapValue newMap(Value... kvs) {
		if (kvs.length == 0) {
			return DImmutableMapValue.empty();
		} else {
			return new DImmutableMapValue(Arrays.copyOf(kvs, kvs.length));
		}
	}

	public static ImmutableMapValue newMap(Value[] kvs, boolean omitCopy) {
		if (kvs.length == 0) {
			return DImmutableMapValue.empty();
		} else if (omitCopy) {
			return new DImmutableMapValue(kvs);
		} else {
			return new DImmutableMapValue(Arrays.copyOf(kvs, kvs.length));
		}
	}

	public static ImmutableMapValue emptyMap() {
		return DImmutableMapValue.empty();
	}

	@SafeVarargs
	public static MapValue newMap(Map.Entry<? extends Value, ? extends Value>... pairs) {
		Value[] kvs = new Value[pairs.length * 2];
		for (int i = 0; i < pairs.length; ++i) {
			kvs[i * 2] = pairs[i].getKey();
			kvs[i * 2 + 1] = pairs[i].getValue();
		}
		return newMap(kvs, true);
	}

	public static MapBuilder newMapBuilder() {
		return new MapBuilder();
	}

	public static Map.Entry<Value, Value> newMapEntry(Value key, Value value) {
		return new AbstractMap.SimpleEntry<>(key, value);
	}

	public static class MapBuilder {

		private final Map<Value, Value> map = new LinkedHashMap<>();

		public MapValue build() {
			return newMap(map);
		}

		public MapBuilder put(Map.Entry<? extends Value, ? extends Value> pair) {
			put(pair.getKey(), pair.getValue());
			return this;
		}

		public MapBuilder put(Value key, Value value) {
			map.put(key, value);
			return this;
		}

		public MapBuilder putAll(Iterable<? extends Map.Entry<? extends Value, ? extends Value>> entries) {
			for (Map.Entry<? extends Value, ? extends Value> entry : entries) put(entry.getKey(), entry.getValue());
			return this;
		}

		public MapBuilder putAll(Map<? extends Value, ? extends Value> map) {
			for (Map.Entry<? extends Value, ? extends Value> entry : map.entrySet()) put(entry);
			return this;
		}
	}

	public static ImmutableExtensionValue newExtension(byte type, byte[] data) {
		return new DImmutableExtensionValue(type, data);
	}

	public static ImmutableTimestampValue newTimestamp(Instant timestamp) {
		return new DImmutableTimestampValue(timestamp);
	}

	public static ImmutableTimestampValue newTimestamp(long millis) {
		return newTimestamp(Instant.ofEpochMilli(millis));
	}

	public static ImmutableTimestampValue newTimestamp(long epochSecond, int nanoAdjustment) {
		return newTimestamp(Instant.ofEpochSecond(epochSecond, nanoAdjustment));
	}
}