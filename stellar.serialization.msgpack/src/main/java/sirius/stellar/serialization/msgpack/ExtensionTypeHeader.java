// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack;

/// Representation of a MessagePack extension type.
///
/// Example usage:
/// ```
/// import sirius.stellar.serialization.msgpack.ExtensionTypeHeader;
/// import static sirius.stellar.serialization.msgpack.ExtensionTypeHeader.checkedCastToByte;
/// ...
/// ExtensionTypeHeader header = new ExtensionTypeHeader(checkedCastToByte(0x01), 32);
/// ```
public class ExtensionTypeHeader {

	private final byte type;
	private final int length;

	/// Creates a new extension type header with the provided type and length.
	///
	/// @param type Extension type as a `byte`. Use [#checkedCastToByte(int)] to
	/// safely cast from an `int` within the valid range.
	/// @param length Length in bytes (must be >=0).
	public ExtensionTypeHeader(byte type, int length) {
		if (length < 0) throw new IllegalArgumentException("length must be >= 0");
		this.type = type;
		this.length = length;
	}

	/// Safely casts an integer to a byte, fails if the value is out of range.
	public static byte checkedCastToByte(int code) {
		byte min = Byte.MIN_VALUE, max = Byte.MAX_VALUE;
		if (min > code || code > max) throw new IllegalArgumentException("Extension type code must be within the range of a byte");
		return (byte) code;
	}

	/// Returns the extension type code.
	public byte type() {
		return this.type;
	}

	/// Returns the length of the extension data in bytes.
	public int length() {
		return this.length;
	}

	/// Returns whether this header represents a timestamp extension type.
	public boolean isTimestampType() {
		return this.type == MessagePack.Code.EXT_TIMESTAMP;
	}

	@Override
	public int hashCode() {
		return (this.type + 31) * 31 + this.length;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof ExtensionTypeHeader) {
			ExtensionTypeHeader other = (ExtensionTypeHeader) object;
			return this.type == other.type && this.length == other.length;
		}
		return false;
	}

	@Override
	public String toString() {
		return "ExtensionTypeHeader[" + "type=" + this.type + ", length=" + this.length + "]";
	}
}