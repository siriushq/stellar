// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack.value;

import sirius.stellar.serialization.msgpack.MessagePacker;
import sirius.stellar.serialization.msgpack.exception.MessageTypeCastException;

import java.io.IOException;

/// Value stores a value and its type in the MessagePack type system.
///
/// ### Type Conversion
/// Check the type using `isXxx()` methods or [#getValueType()], then convert to a subtype using `asXxx()` methods.
/// Alternatively, call `asXxx()` methods directly and handle [MessageTypeCastException].
///
/// | MessagePack type   | Check method          | Convert method        | Value type       |
/// |--------------------|-----------------------|-----------------------|------------------|
/// | Nil                | [#isNilValue()]       | [#asNilValue()]       | [NilValue]       |
/// | Boolean            | [#isBooleanValue()]   | [#asBooleanValue()]   | [BooleanValue]   |
/// | Integer or Float   | [#isNumberValue()]    | [#asNumberValue()]    | [NumberValue]    |
/// | Integer            | [#isIntegerValue()]   | [#asIntegerValue()]   | [IntegerValue]   |
/// | Float              | [#isFloatValue()]     | [#asFloatValue()]     | [FloatValue]     |
/// | String or Binary   | [#isRawValue()]       | [#asRawValue()]       | [RawValue]       |
/// | String             | [#isStringValue()]    | [#asStringValue()]    | [StringValue]    |
/// | Binary             | [#isBinaryValue()]    | [#asBinaryValue()]    | [BinaryValue]    |
/// | Array              | [#isArrayValue()]     | [#asArrayValue()]     | [ArrayValue]     |
/// | Map                | [#isMapValue()]       | [#asMapValue()]       | [MapValue]       |
/// | Extension          | [#isExtensionValue()] | [#asExtensionValue()] | [ExtensionValue] |
///
/// ### Immutable Interface
/// The [Value] interface is the base for all value interfaces. Immutable subtypes are useful for declaring
/// immutable fields or container elements. Use the [Value] interface for method arguments generally.
/// Obtain immutable subtypes using [#immutableValue()].
///
/// | MessagePack type   | Subtype method                    | Immutable value type      |
/// |--------------------|-----------------------------------|---------------------------|
/// | Any                | [Value#immutableValue()]          | [ImmutableValue]          |
/// | Nil                | [NilValue#immutableValue()]       | [ImmutableNilValue]       |
/// | Boolean            | [BooleanValue#immutableValue()]   | [ImmutableBooleanValue]   |
/// | Integer            | [IntegerValue#immutableValue()]   | [ImmutableIntegerValue]   |
/// | Float              | [FloatValue#immutableValue()]     | [ImmutableFloatValue]     |
/// | Integer or Float   | [NumberValue#immutableValue()]    | [ImmutableNumberValue]    |
/// | String or Binary   | [RawValue#immutableValue()]       | [ImmutableRawValue]       |
/// | String             | [StringValue#immutableValue()]    | [ImmutableStringValue]    |
/// | Binary             | [BinaryValue#immutableValue()]    | [ImmutableBinaryValue]    |
/// | Array              | [ArrayValue#immutableValue()]     | [ImmutableArrayValue]     |
/// | Map                | [MapValue#immutableValue()]       | [ImmutableMapValue]       |
/// | Extension          | [ExtensionValue#immutableValue()] | [ImmutableExtensionValue] |
///
/// ### Converting to JSON
/// Use [#toJson()] to get a JSON representation of a [Value]. See its documentation for details.
/// The `toString()` method also provides a string representation similar to JSON, but may use
/// a special format for types like [ExtensionValue] that JSON does not support.
public interface Value {

	/// Returns the [ValueType] of this value.
	/// Use this method instead of `instanceof` to check the type, as the type of a mutable value may vary.
	ValueType getValueType();

	/// Returns an immutable copy of this value as [ImmutableValue].
	/// Returns `this` without copying if the value is already immutable.
	ImmutableValue immutableValue();

	/// Returns `true` if the value is Nil.
	///
	/// Use this method instead of `instanceof` or casting to [NilValue], as the type of a mutable value may vary.
	/// If `true`, [#asNilValue()] will not throw exceptions.
	boolean isNilValue();

	/// Returns `true` if the value is Boolean.
	///
	/// Use this method instead of `instanceof` or casting to [BooleanValue], as the type of a mutable value may vary.
	/// If `true`, [#asBooleanValue()] will not throw exceptions.
	boolean isBooleanValue();

	/// Returns `true` if the value is Integer or Float.
	///
	/// Use this method instead of `instanceof` or casting to [NumberValue], as the type of a mutable value may vary.
	/// If `true`, [#asNumberValue()] will not throw exceptions.
	boolean isNumberValue();

	/// Returns `true` if the value is Integer.
	///
	/// Use this method instead of `instanceof` or casting to [IntegerValue], as the type of a mutable value may vary.
	/// If `true`, [#asIntegerValue()] will not throw exceptions.
	boolean isIntegerValue();

	/// Returns `true` if the value is Float.
	///
	/// Use this method instead of `instanceof` or casting to [FloatValue], as the type of a mutable value may vary.
	/// If `true`, [#asFloatValue()] will not throw exceptions.
	boolean isFloatValue();

	/// Returns `true` if the value is String or Binary.
	///
	/// Use this method instead of `instanceof` or casting to [RawValue], as the type of a mutable value may vary.
	/// If `true`, [#asRawValue()] will not throw exceptions.
	boolean isRawValue();

	/// Returns `true` if the value is Binary.
	///
	/// Use this method instead of `instanceof` or casting to [BinaryValue], as the type of a mutable value may vary.
	/// If `true`, [#asBinaryValue()] will not throw exceptions.
	boolean isBinaryValue();

	/// Returns `true` if the value is String.
	///
	/// Use this method instead of `instanceof` or casting to [StringValue], as the type of a mutable value may vary.
	/// If `true`, [#asStringValue()] will not throw exceptions.
	boolean isStringValue();

	/// Returns `true` if the value is Array.
	///
	/// Use this method instead of `instanceof` or casting to [ArrayValue], as the type of a mutable value may vary.
	/// If `true`, [#asArrayValue()] will not throw exceptions.
	boolean isArrayValue();

	/// Returns `true` if the value is Map.
	///
	/// Use this method instead of `instanceof` or casting to [MapValue], as the type of a mutable value may vary.
	/// If `true`, [#asMapValue()] will not throw exceptions.
	boolean isMapValue();

	/// Returns `true` if the value is Extension.
	///
	/// Use this method instead of `instanceof` or casting to [ExtensionValue], as the type of a mutable value may vary.
	/// If `true`, [#asExtensionValue()] will not throw exceptions.
	boolean isExtensionValue();

	/// Returns `true` if the value is Timestamp.
	///
	/// Use this method instead of `instanceof` or casting to [TimestampValue], as the type of a mutable value may vary.
	/// If `true`, [#asTimestampValue()] will not throw exceptions.
	boolean isTimestampValue();

	/// Converts the value to [NilValue].
	///
	/// Use [#isNilValue()] instead of `instanceof` or casting to [NilValue], as the type of a mutable value may vary.
	/// @throws MessageTypeCastException if the value is not Nil.
	NilValue asNilValue();

	/// Converts the value to [BooleanValue].
	///
	/// Use [#isBooleanValue()] instead of `instanceof` or casting to [BooleanValue], as the type of a mutable value may vary.
	/// @throws MessageTypeCastException if the value is not Boolean.
	BooleanValue asBooleanValue();

	/// Converts the value to [NumberValue].
	///
	/// Use [#isNumberValue()] instead of `instanceof` or casting to [NumberValue], as the type of a mutable value may vary.
	/// @throws MessageTypeCastException if the value is not Integer or Float.
	NumberValue asNumberValue();

	/// Converts the value to [IntegerValue].
	///
	/// Use [#isIntegerValue()] instead of `instanceof` or casting to [IntegerValue], as the type of a mutable value may vary.
	/// @throws MessageTypeCastException if the value is not Integer.
	IntegerValue asIntegerValue();

	/// Converts the value to [FloatValue].
	///
	/// Use [#isFloatValue()] instead of `instanceof` or casting to [FloatValue], as the type of a mutable value may vary.
	/// @throws MessageTypeCastException if the value is not Float.
	FloatValue asFloatValue();

	/// Converts the value to [RawValue].
	///
	/// Use [#isRawValue()] instead of `instanceof` or casting to [RawValue], as the type of a mutable value may vary.
	/// @throws MessageTypeCastException if the value is not String or Binary.
	RawValue asRawValue();

	/// Converts the value to [StringValue].
	///
	/// Use [#isStringValue()] instead of `instanceof` or casting to [StringValue], as the type of a mutable value may vary.
	/// @throws MessageTypeCastException if the value is not String.
	StringValue asStringValue();

	/// Converts the value to [BinaryValue].
	///
	/// Use [#isBinaryValue()] instead of `instanceof` or casting to [BinaryValue], as the type of a mutable value may vary.
	/// @throws MessageTypeCastException if the value is not Binary.
	BinaryValue asBinaryValue();

	/// Converts the value to [ArrayValue].
	///
	/// Use [#isArrayValue()] instead of `instanceof` or casting to [ArrayValue], as the type of a mutable value may vary.
	/// @throws MessageTypeCastException if the value is not Array.
	ArrayValue asArrayValue();

	/// Converts the value to [MapValue].
	///
	/// Use [#isMapValue()] instead of `instanceof` or casting to [MapValue], as the type of a mutable value may vary.
	/// @throws MessageTypeCastException if the value is not Map.
	MapValue asMapValue();

	/// Converts the value to [ExtensionValue].
	///
	/// Use [#isExtensionValue()] instead of `instanceof` or casting to [ExtensionValue], as the type of a mutable value may vary.
	/// @throws MessageTypeCastException if the value is not Extension.
	ExtensionValue asExtensionValue();

	/// Converts the value to [TimestampValue].
	///
	/// Use [#isTimestampValue()] instead of `instanceof` or casting to [TimestampValue], as the type of a mutable value may vary.
	/// @throws MessageTypeCastException if the value is not Timestamp.
	TimestampValue asTimestampValue();

	/// Serializes the value using the specified [MessagePacker].
	void writeTo(MessagePacker pk) throws IOException;

	/// Checks if this value is equivalent to the specified object.
	///
	/// Returns `true` if the type and value are equivalent.
	/// For [MapValue] or [ArrayValue], recursively compares elements.
	boolean equals(Object obj);

	/// Returns the JSON representation of this value.
	///
	/// Non-configurable behaviors (may change in future releases):
	/// - Non-string keys in [MapValue] are converted to strings using `toString()`.
	/// - NaN and Infinity of `DoubleValue` are converted to `null`.
	/// - [ExtensionValue] is converted to a 2-element array: (type number, hex-encoded data).
	/// - [BinaryValue] is converted to a string using UTF-8, with invalid sequences replaced by U+FFFD.
	/// - Invalid UTF-8 sequences in [StringValue] are replaced by U+FFFD.
	String toJson();
}