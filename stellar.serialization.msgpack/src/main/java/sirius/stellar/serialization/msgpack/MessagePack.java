// SPDX-License-Identifier: BSD-3-Clause AND Apache-2.0
package sirius.stellar.serialization.msgpack;

import sirius.stellar.serialization.msgpack.buffer.ArrayBufferInput;
import sirius.stellar.serialization.msgpack.buffer.ByteBufferInput;
import sirius.stellar.serialization.msgpack.buffer.ChannelBufferInput;
import sirius.stellar.serialization.msgpack.buffer.ChannelBufferOutput;
import sirius.stellar.serialization.msgpack.buffer.InputStreamBufferInput;
import sirius.stellar.serialization.msgpack.buffer.MessageBufferInput;
import sirius.stellar.serialization.msgpack.buffer.MessageBufferOutput;
import sirius.stellar.serialization.msgpack.buffer.OutputStreamBufferOutput;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CodingErrorAction;

/// Convenience class to build packer and unpacker classes.
/// You can select an appropriate factory method as following.
///
/// Deserializing objects from binary:
///
/// | Input type           | Factory method                             | Return type       |
/// |----------------------|--------------------------------------------|-------------------|
/// | byte[]               | [#newDefaultUnpacker(byte[],int,int)]      | [MessageUnpacker] |
/// | ByteBuffer           | [#newDefaultUnpacker(ByteBuffer)]          | [MessageUnpacker] |
/// | InputStream          | [#newDefaultUnpacker(InputStream)]         | [MessageUnpacker] |
/// | ReadableByteChannel  | [#newDefaultUnpacker(ReadableByteChannel)] | [MessageUnpacker] |
/// | [MessageBufferInput] | [#newDefaultUnpacker(MessageBufferInput)]  | [MessageUnpacker] |
///
/// Serializing objects into binary:
///
/// | Output type           | Factory method                           | Return type           |
/// |-----------------------|------------------------------------------|-----------------------|
/// | byte[]                | [#newDefaultBufferPacker()]              | [MessageBufferPacker] |
/// | OutputStream          | [#newDefaultPacker(OutputStream)]        | [MessagePacker]       |
/// | WritableByteChannel   | [#newDefaultPacker(WritableByteChannel)] | [MessagePacker]       |
/// | [MessageBufferOutput] | [#newDefaultPacker(MessageBufferOutput)] | [MessagePacker]       |
public class MessagePack {

	/// Configuration of a [MessagePacker] used by [#newDefaultPacker(MessageBufferOutput)] and [#newDefaultBufferPacker()].
	public static final PackerConfig DEFAULT_PACKER_CONFIG = new PackerConfig();

    /// Configuration of a [MessageUnpacker] used by [#newDefaultUnpacker(MessageBufferInput)].
    public static final UnpackerConfig DEFAULT_UNPACKER_CONFIG = new UnpackerConfig();

	private MessagePack() {
		throw new IllegalStateException();
	}

    /// Creates a packer that serializes objects into the specified output.
    ///
    /// [MessageBufferOutput] is an interface that lets applications customize memory
    /// allocation of internal buffer of [MessagePacker].
    ///
    /// @param output Instance of [MessageBufferOutput] that allocates buffer chunks
	/// and receives the buffer chunks with packed data filled in them.
    public static MessagePacker newDefaultPacker(MessageBufferOutput output) {
        return DEFAULT_PACKER_CONFIG.newPacker(output);
    }

    /// Creates a packer that serializes objects into the specified output stream.
    ///
    /// Note that you don't have to wrap [OutputStream] in [BufferedOutputStream]
	/// because [MessagePacker] has buffering internally.
    public static MessagePacker newDefaultPacker(OutputStream output) {
        return DEFAULT_PACKER_CONFIG.newPacker(output);
    }

    /// Creates a packer that serializes objects into the specified writable channel.
    public static MessagePacker newDefaultPacker(WritableByteChannel channel) {
        return DEFAULT_PACKER_CONFIG.newPacker(channel);
    }

    /// Creates a packer that serializes objects into byte arrays.
    /// This method provides an optimized implementation of `newDefaultBufferPacker(new ByteArrayOutputStream())`.
    public static MessageBufferPacker newDefaultBufferPacker() {
        return DEFAULT_PACKER_CONFIG.newBufferPacker();
    }

    /// Creates an unpacker that deserializes objects from a specified input.
    ///
    /// [MessageBufferInput] is an interface that lets applications customize memory allocation
	/// of the internal buffer of [MessageUnpacker].
    ///
    /// @param input The input stream that provides the sequence of buffer chunks and optionally
	/// reuses them when [MessageUnpacker] has consumed one completely.
	public static MessageUnpacker newDefaultUnpacker(MessageBufferInput input) {
		return DEFAULT_UNPACKER_CONFIG.newUnpacker(input);
	}

    /// Creates an unpacker that deserializes objects from a specified input stream.
    ///
    /// Note that you don't have to wrap [InputStream] in [BufferedInputStream] because
	/// [MessageUnpacker] has buffering internally.
    public static MessageUnpacker newDefaultUnpacker(InputStream input) {
        return DEFAULT_UNPACKER_CONFIG.newUnpacker(input);
    }

    /// Creates an unpacker that deserializes objects from a specified readable channel.
    public static MessageUnpacker newDefaultUnpacker(ReadableByteChannel channel) {
        return DEFAULT_UNPACKER_CONFIG.newUnpacker(channel);
    }

    /// Creates an unpacker that deserializes objects from a specified byte array.
    /// This method provides an optimized implementation of `newDefaultUnpacker(new ByteArrayInputStream(contents))`.
    public static MessageUnpacker newDefaultUnpacker(byte[] contents) {
        return DEFAULT_UNPACKER_CONFIG.newUnpacker(contents);
    }

    /// Creates an unpacker that deserializes objects from subarray of a specified byte array.
    /// This method provides an optimized implementation of `newDefaultUnpacker(new ByteArrayInputStream(contents, offset, length))`.
    public static MessageUnpacker newDefaultUnpacker(byte[] contents, int offset, int length) {
        return DEFAULT_UNPACKER_CONFIG.newUnpacker(contents, offset, length);
    }

    /// Creates an unpacker that deserializes objects from a specified ByteBuffer.
    ///
    /// Note that the returned unpacker reads data from the current position of the [ByteBuffer] until its limit.
    /// However, its position does not change when the unpacker reads data.
	///
	/// You may use [MessageUnpacker#getTotalReadBytes()] to get the actual amount of bytes used in the [ByteBuffer].
    /// This method supports both non-direct buffer and direct buffer.
	public static MessageUnpacker newDefaultUnpacker(ByteBuffer contents) {
		return DEFAULT_UNPACKER_CONFIG.newUnpacker(contents);
	}

    /// Configuration for [MessagePacker].
	public static class PackerConfig implements Cloneable {

        private int smallStringOptimizationThreshold;
        private int bufferFlushThreshold;
        private int bufferSize;
        private boolean str8FormatSupport;

		public PackerConfig() {
			this.smallStringOptimizationThreshold = 512;
			this.bufferFlushThreshold = 8192;
			this.bufferSize = 8192;
			this.str8FormatSupport = true;
		}

		private PackerConfig(PackerConfig copy) {
			this.smallStringOptimizationThreshold = copy.smallStringOptimizationThreshold;
			this.bufferFlushThreshold = copy.bufferFlushThreshold;
			this.bufferSize = copy.bufferSize;
			this.str8FormatSupport = copy.str8FormatSupport;
		}

		@Override
		public PackerConfig clone() {
			return new PackerConfig(this);
		}

		@Override
		public int hashCode() {
			int result = this.smallStringOptimizationThreshold;
			result = 31 * result + this.bufferFlushThreshold;
			result = 31 * result + this.bufferSize;
			result = 31 * result + (this.str8FormatSupport ? 1 : 0);
			return result;
		}

		@Override
		public boolean equals(Object object) {
			if (!(object instanceof PackerConfig)) return false;
			PackerConfig other = (PackerConfig) object;
			return this.smallStringOptimizationThreshold == other.smallStringOptimizationThreshold &&
					this.bufferFlushThreshold == other.bufferFlushThreshold &&
					this.bufferSize == other.bufferSize &&
					this.str8FormatSupport == other.str8FormatSupport;
		}

		/// Creates a packer that serializes objects into the specified output.
		/// [MessageBufferOutput] allocates and receives buffer chunks with packed data.
		public MessagePacker newPacker(MessageBufferOutput output) {
			return new MessagePacker(output, this);
		}

		/// Creates a packer that serializes objects into the specified output stream.
		///
		/// Note that you don't have to wrap [OutputStream] in [BufferedOutputStream]
		/// because [MessagePacker] has buffering internally.
		public MessagePacker newPacker(OutputStream output) {
			return newPacker(new OutputStreamBufferOutput(output, bufferSize));
		}

		/// Creates a packer that serializes objects into the specified writable channel.
		public MessagePacker newPacker(WritableByteChannel channel) {
			return newPacker(new ChannelBufferOutput(channel, bufferSize));
		}

		/// Creates a packer that serializes objects into byte arrays.
		/// This method provides an optimized implementation of `newDefaultBufferPacker(new ByteArrayOutputStream())`.
		public MessageBufferPacker newBufferPacker() {
			return new MessageBufferPacker(this);
		}

		/// Use `String.getBytes()` for converting Java Strings that are shorter than this threshold.
		/// Note that this parameter is subject to change.
		public PackerConfig smallStringOptimizationThreshold(int length) {
			PackerConfig copy = clone();
			copy.smallStringOptimizationThreshold = length;
			return copy;
		}

		public int smallStringOptimizationThreshold() {
			return smallStringOptimizationThreshold;
		}

		/// When the next payload size exceeds this threshold, [MessagePacker] will call
		/// [MessageBufferOutput#flush()] before writing more data, by default `8192`.
		public PackerConfig bufferFlushThreshold(int bytes) {
			PackerConfig copy = clone();
			copy.bufferFlushThreshold = bytes;
			return copy;
		}

		public int bufferFlushThreshold() {
			return bufferFlushThreshold;
		}

		/// When a packer is created with [#newPacker(OutputStream)] or [#newPacker(WritableByteChannel)],
		/// the stream will be buffered with this size of buffer, by default `8192`.
		public PackerConfig bufferSize(int bytes) {
			PackerConfig copy = clone();
			copy.bufferSize = bytes;
			return copy;
		}

		public int bufferSize() {
			return bufferSize;
		}

		/// Disable str8 format when needed for backward compatibility between
		/// different msgpack serializer versions, by default `true` (support enabled).
		public PackerConfig str8FormatSupport(boolean str8FormatSupport) {
			PackerConfig copy = clone();
			copy.str8FormatSupport = str8FormatSupport;
			return copy;
		}

		public boolean str8FormatSupport() {
			return str8FormatSupport;
		}
    }

	/// Configuration for [MessageUnpacker].
	public static class UnpackerConfig implements Cloneable {

		private boolean allowReadingStringAsBinary;
		private boolean allowReadingBinaryAsString;

		private CodingErrorAction actionOnMalformedString;
		private CodingErrorAction actionOnUnmappableString;

		private int stringSizeLimit;
		private int bufferSize;
		private int stringDecoderBufferSize;

		public UnpackerConfig() {
			this.allowReadingStringAsBinary = true;
			this.allowReadingBinaryAsString = true;

			this.actionOnMalformedString = CodingErrorAction.REPLACE;
			this.actionOnUnmappableString = CodingErrorAction.REPLACE;

			this.stringSizeLimit = Integer.MAX_VALUE;
			this.bufferSize = 8192;
			this.stringDecoderBufferSize = 8192;
		}

		private UnpackerConfig(UnpackerConfig copy) {
			this.allowReadingStringAsBinary = copy.allowReadingStringAsBinary;
			this.allowReadingBinaryAsString = copy.allowReadingBinaryAsString;

			this.actionOnMalformedString = copy.actionOnMalformedString;
			this.actionOnUnmappableString = copy.actionOnUnmappableString;

			this.stringSizeLimit = copy.stringSizeLimit;
			this.bufferSize = copy.bufferSize;
			this.stringDecoderBufferSize = copy.stringDecoderBufferSize;
		}

		@Override
		public UnpackerConfig clone() {
			return new UnpackerConfig(this);
		}

		@Override
		public int hashCode() {
			int result = (this.allowReadingStringAsBinary ? 1 : 0);
			result = 31 * result + (this.allowReadingBinaryAsString ? 1 : 0);
			result = 31 * result + (this.actionOnMalformedString != null ? this.actionOnMalformedString.hashCode() : 0);
			result = 31 * result + (this.actionOnUnmappableString != null ? this.actionOnUnmappableString.hashCode() : 0);
			result = 31 * result + this.stringSizeLimit;
			result = 31 * result + this.bufferSize;
			result = 31 * result + this.stringDecoderBufferSize;
			return result;
		}

		@Override
		public boolean equals(Object object) {
			if (!(object instanceof UnpackerConfig)) return false;
			UnpackerConfig other = (UnpackerConfig) object;
			return (this.allowReadingStringAsBinary == other.allowReadingStringAsBinary) &&
					(this.allowReadingBinaryAsString == other.allowReadingBinaryAsString) &&
					(this.actionOnMalformedString == other.actionOnMalformedString) &&
					(this.actionOnUnmappableString == other.actionOnUnmappableString) &&
					(this.stringSizeLimit == other.stringSizeLimit) &&
					(this.stringDecoderBufferSize == other.stringDecoderBufferSize) &&
					(this.bufferSize == other.bufferSize);
		}

		/// Creates an unpacker that deserializes objects from a specified input.
		///
		/// [MessageBufferInput] provides the sequence of buffer chunks and optionally
		/// reuses them when [MessageUnpacker] has consumed one completely.
		public MessageUnpacker newUnpacker(MessageBufferInput input) {
			return new MessageUnpacker(input, this);
		}

		/// Creates an unpacker that deserializes objects from a specified input stream.
		///
		/// Note that you don't have to wrap [InputStream] in [BufferedInputStream]
		/// because [MessageUnpacker] has buffering internally.
		public MessageUnpacker newUnpacker(InputStream input) {
			return newUnpacker(new InputStreamBufferInput(input, bufferSize));
		}

		/// Creates an unpacker that deserializes objects from a specified readable channel.
		public MessageUnpacker newUnpacker(ReadableByteChannel channel) {
			return newUnpacker(new ChannelBufferInput(channel, bufferSize));
		}

		/// Creates an unpacker that deserializes objects from a specified byte array.
		/// This method provides an optimized implementation of `newDefaultUnpacker(new ByteArrayInputStream(contents))`.
		public MessageUnpacker newUnpacker(byte[] contents) {
			return newUnpacker(new ArrayBufferInput(contents));
		}

		/// Creates an unpacker that deserializes objects from a subarray of a specified byte array.
		/// This method provides an optimized implementation of `newDefaultUnpacker(new ByteArrayInputStream(contents, offset, length))`.
		public MessageUnpacker newUnpacker(byte[] contents, int offset, int length) {
			return newUnpacker(new ArrayBufferInput(contents, offset, length));
		}

		/// Creates an unpacker that deserializes objects from a specified [ByteBuffer].
		///
		/// Reads from the current position until the limit without changing the position.
		/// Use [MessageUnpacker#getTotalReadBytes()] to get the actual amount of bytes used.
		public MessageUnpacker newUnpacker(ByteBuffer contents) {
			return newUnpacker(new ByteBufferInput(contents));
		}

		/// Allows `unpackBinaryHeader` to read str format family, by default `true`.
		public UnpackerConfig allowReadingStringAsBinary(boolean enable) {
			UnpackerConfig copy = clone();
			copy.allowReadingStringAsBinary = enable;
			return copy;
		}

		public boolean allowReadingStringAsBinary() {
			return allowReadingStringAsBinary;
		}

		/// Allows `unpackString`, `unpackRawStringHeader` and `unpackString` to read bin format family, by default `true`.
		public UnpackerConfig allowReadingBinaryAsString(boolean enable) {
			UnpackerConfig copy = clone();
			copy.allowReadingBinaryAsString = enable;
			return copy;
		}

		public boolean allowReadingBinaryAsString() {
			return allowReadingBinaryAsString;
		}

		/// Sets action when encountered a malformed input, by default `REPLACE`.
		public UnpackerConfig actionOnMalformedString(CodingErrorAction action) {
			UnpackerConfig copy = clone();
			copy.actionOnMalformedString = action;
			return copy;
		}

		public CodingErrorAction actionOnMalformedString() {
			return actionOnMalformedString;
		}

		/// Sets action when an unmappable character is found, by default `REPLACE`.
		public UnpackerConfig actionOnUnmappableString(CodingErrorAction action) {
			UnpackerConfig copy = clone();
			copy.actionOnUnmappableString = action;
			return copy;
		}

		public CodingErrorAction actionOnUnmappableString() {
			return actionOnUnmappableString;
		}

		/// `unpackString` size limit, by default [Integer#MAX_VALUE].
		public UnpackerConfig stringSizeLimit(int bytes) {
			UnpackerConfig copy = clone();
			copy.stringSizeLimit = bytes;
			return copy;
		}

		public int stringSizeLimit() {
			return stringSizeLimit;
		}

		public UnpackerConfig stringDecoderBufferSize(int bytes) {
			UnpackerConfig copy = clone();
			copy.stringDecoderBufferSize = bytes;
			return copy;
		}

		public int stringDecoderBufferSize() {
			return stringDecoderBufferSize;
		}

		/// When a packer is created with `newUnpacker(OutputStream)` or `newUnpacker(WritableByteChannel)`,
		/// the stream will be buffered with this buffer size, by default `8192`.
		public UnpackerConfig bufferSize(int bytes) {
			UnpackerConfig copy = clone();
			copy.bufferSize = bytes;
			return copy;
		}

		public int bufferSize() {
			return bufferSize;
		}
	}

	/// The prefix code set of MessagePack format.
	public static final class Code {

		public static final byte POSFIXINT_MASK = (byte) 0x80;

		public static final byte FIXMAP_PREFIX = (byte) 0x80;
		public static final byte FIXARRAY_PREFIX = (byte) 0x90;
		public static final byte FIXSTR_PREFIX = (byte) 0xa0;

		public static final byte NIL = (byte) 0xc0;
		public static final byte NEVER_USED = (byte) 0xc1;
		public static final byte FALSE = (byte) 0xc2;
		public static final byte TRUE = (byte) 0xc3;
		public static final byte BIN8 = (byte) 0xc4;
		public static final byte BIN16 = (byte) 0xc5;
		public static final byte BIN32 = (byte) 0xc6;
		public static final byte EXT8 = (byte) 0xc7;
		public static final byte EXT16 = (byte) 0xc8;
		public static final byte EXT32 = (byte) 0xc9;
		public static final byte FLOAT32 = (byte) 0xca;
		public static final byte FLOAT64 = (byte) 0xcb;
		public static final byte UINT8 = (byte) 0xcc;
		public static final byte UINT16 = (byte) 0xcd;
		public static final byte UINT32 = (byte) 0xce;
		public static final byte UINT64 = (byte) 0xcf;

		public static final byte INT8 = (byte) 0xd0;
		public static final byte INT16 = (byte) 0xd1;
		public static final byte INT32 = (byte) 0xd2;
		public static final byte INT64 = (byte) 0xd3;

		public static final byte FIXEXT1 = (byte) 0xd4;
		public static final byte FIXEXT2 = (byte) 0xd5;
		public static final byte FIXEXT4 = (byte) 0xd6;
		public static final byte FIXEXT8 = (byte) 0xd7;
		public static final byte FIXEXT16 = (byte) 0xd8;

		public static final byte STR8 = (byte) 0xd9;
		public static final byte STR16 = (byte) 0xda;
		public static final byte STR32 = (byte) 0xdb;

		public static final byte ARRAY16 = (byte) 0xdc;
		public static final byte ARRAY32 = (byte) 0xdd;

		public static final byte MAP16 = (byte) 0xde;
		public static final byte MAP32 = (byte) 0xdf;

		public static final byte NEGFIXINT_PREFIX = (byte) 0xe0;

		public static final byte EXT_TIMESTAMP = (byte) -1;

		public static boolean isFixInt(byte b) {
			int v = b & 0xFF;
			return v <= 0x7f || v >= 0xe0;
		}

		public static boolean isPosFixInt(byte b) {
			return (b & POSFIXINT_MASK) == 0;
		}

		public static boolean isNegFixInt(byte b) {
			return (b & NEGFIXINT_PREFIX) == NEGFIXINT_PREFIX;
		}

		public static boolean isFixStr(byte b) {
			return (b & (byte) 0xe0) == FIXSTR_PREFIX;
		}

		public static boolean isFixedArray(byte b) {
			return (b & (byte) 0xf0) == FIXARRAY_PREFIX;
		}

		public static boolean isFixedMap(byte b) {
			return (b & (byte) 0xf0) == FIXMAP_PREFIX;
		}

		public static boolean isFixedRaw(byte b) {
			return (b & (byte) 0xe0) == FIXSTR_PREFIX;
		}
	}
}