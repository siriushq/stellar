// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.value;

/// Representation of MessagePack types.
///
/// MessagePack uses a hierarchical type system. Integer and Float are subtype of Number, thus [#isNumberType()]
/// returns `true` if type is Integer or Float. String and Binary are subtype of Raw, thus [#isRawType()] returns
/// `true` if type is String or Binary.
///
/// Timestamp is not a [ValueType] because detecting Timestamp values requires reading 1-3 bytes ahead while
/// the other value types can be determined just by reading the first one byte.
///
/// @see sirius.stellar.serialization.msgpack.MessageFormat
public enum ValueType {

	NIL(false, false),
	BOOLEAN(false, false),
	INTEGER(true, false),
	FLOAT(true, false),
	STRING(false, true),
	BINARY(false, true),
	ARRAY(false, false),
	MAP(false, false),
	EXTENSION(false, false);

	private final boolean numberType;
	private final boolean rawType;

	ValueType(boolean numberType, boolean rawType) {
		this.numberType = numberType;
		this.rawType = rawType;
	}

	public boolean isNilType() {
		return this == NIL;
	}

	public boolean isBooleanType() {
		return this == BOOLEAN;
	}

	public boolean isNumberType() {
		return numberType;
	}

	public boolean isIntegerType() {
		return this == INTEGER;
	}

	public boolean isFloatType() {
		return this == FLOAT;
	}

	public boolean isRawType() {
		return rawType;
	}

	public boolean isStringType() {
		return this == STRING;
	}

	public boolean isBinaryType() {
		return this == BINARY;
	}

	public boolean isArrayType() {
		return this == ARRAY;
	}

	public boolean isMapType() {
		return this == MAP;
	}

	public boolean isExtensionType() {
		return this == EXTENSION;
	}
}