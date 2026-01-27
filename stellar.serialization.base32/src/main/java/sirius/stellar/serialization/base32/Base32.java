package sirius.stellar.serialization.base32;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;

import static java.lang.Math.ceil;

/// This interface consists exclusively of static methods for encoding and
/// decoding payloads in the RFC 4648 Base32 encoding scheme.
public interface Base32 {

	/// Lookup table that represents a [Base32] alphabet, A-Z followed by 2-7.
	char[] ALPHABET = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
		'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		'2', '3', '4', '5', '6', '7'
	};

	/// Inverse ASCII lookup table for [Base32] characters, for ASCII 0-95.
	byte[] ASCII = {
		// 0-31 (control characters)
		-1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1,

		// 32-47 (symbols)
		-1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1,

		// 48-55 ('2' - '7')
		-1, -1, 26, 27, 28, 29, 30, 31,

		// 56-63
		-1, -1, -1, -1, -1, -1, -1, -1,

		// 64-79 ('A' - 'O')
		-1, 0, 1, 2, 3, 4, 5, 6,
		7, 8, 9, 10, 11, 12, 13, 14,

		// 80-95 ('P' - 'Z')
		15, 16, 17, 18, 19, 20, 21, 22,
		23, 24, 25, -1, -1, -1, -1, -1
	};

	/// Encode the provided input, writing to an [Appendable] reference.
	/// If the provided reference is `null`, nothing will be written.
	static void encode(byte[] input, @Nullable Appendable output) {
		if (output == null) return;

		int buffer = 0;
		int remaining = 0;

		for (byte b : input) {
			buffer = (buffer << 8) | (b & 0xFF);
			remaining += 8;

			while (remaining >= 5) {
				remaining -= 5;
				char digit = ALPHABET[(buffer >> remaining) & 0x1F];
				append(output, digit);
			}
		}

		if (remaining > 0) {
			remaining = 5 - remaining;
			char digit = ALPHABET[(buffer << remaining) & 0x1F];
			append(output, digit);
		}
	}

	/// Decodes the provided input into the provided output buffer.
	/// @return The number of bytes written.
	static int decode(char[] input, byte[] output) {
		int buffer = 0;
		int remaining = 0;
		int written = 0;

		for (char c : input) {
			if (c >= ASCII.length) continue;

			int digit = ASCII[c];
			if (digit < 0) continue;

			buffer = (buffer << 5) | digit;
			remaining += 5;

			if (remaining >= 8) {
				remaining -= 8;
				output[written++] = (byte) (buffer >> remaining);
			}
		}

		return written;
	}

	/// Encode the provided input to a new [String].
	static String encode(byte[] input) {
		int size = (int) ceil(input.length * 8 / 5d);
		StringBuilder builder = new StringBuilder(size);

		encode(input, builder);
		return builder.toString();
	}

	/// Decodes the provided input into a new `byte[]`.
	/// @return [ByteBuffer] view of the new array.
	static ByteBuffer decode(char[] input) {
		byte[] buffer = new byte[input.length];
		int written = decode(input, buffer);
		return ByteBuffer.wrap(buffer, 0, written);
	}

	/// Decodes the provided input into a new `byte[]`.
	/// @return [ByteBuffer] view of the new array.
	static ByteBuffer decode(String input) {
		return decode(input.toCharArray());
	}

	/// Append `char`s to [Appendable] references without checked exceptions.
	private static void append(Appendable appendable, char c) {
		try {
			appendable.append(c);
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}
}