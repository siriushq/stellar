package sirius.stellar.serialization.msgpack.value;

import sirius.stellar.serialization.msgpack.exception.MessageStringCodingException;

import java.nio.ByteBuffer;

/// Base interface of [StringValue] and [BinaryValue] interfaces. \
/// MessagePack's Raw type can represent a byte array at most 2<sup>64</sup>-1 bytes.
///
/// @see sirius.stellar.serialization.msgpack.value.StringValue
/// @see sirius.stellar.serialization.msgpack.value.BinaryValue
public interface RawValue extends Value {

	/// Returns the value as `byte[]`. \
	/// This method copies the byte array.
	byte[] asByteArray();

	/// Returns the value as `ByteBuffer`. \
	/// Returned ByteBuffer is read-only. See also [#asReadOnlyBuffer()].
	/// This method doesn't copy the byte array as much as possible.
	ByteBuffer asByteBuffer();

	/// Returns the value as `String`. \
	/// This method throws an exception if the value includes invalid UTF-8 byte sequence.
	/// @throws MessageStringCodingException If this value includes invalid UTF-8 byte sequence.
	String asString();

	/// Returns the value as `String`. \
	/// This method replaces an invalid UTF-8 byte sequence with <code>U+FFFD replacement character</code>.
	String toString();
}