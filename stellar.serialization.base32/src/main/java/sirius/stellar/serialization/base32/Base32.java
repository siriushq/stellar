package sirius.stellar.serialization.base32;

/// This interface consists exclusively of static methods for encoding and
/// decoding payloads in the RFC 4648 Base32 encoding scheme.
///
/// {@snippet lang = "java":
/// byte[] payload = "Hello, world!".getBytes();
///
/// char[] encoded = Base32.encode(payload);
/// println(encoded);
///
/// byte[] decoded = Base32.decode(encoded);
/// println(decoded);
/// }
public interface Base32 {

	/// Lookup table that represents a [Base32] alphabet, A-Z followed by 2-7.
	char[] ALPHABET = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
		'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		'2', '3', '4', '5', '6', '7'
	};

	/// Inverse ASCII lookup table for [Base32] characters, for ASCII 0-127.
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
		23, 24, 25, -1, -1, -1, -1, -1,

		// 96-111 ('`', 'a' - 'o')
		-1, 0, 1, 2, 3, 4, 5, 6,
		7, 8, 9, 10, 11, 12, 13, 14,

		// 112-127 ('p' - 'z')
		15, 16, 17, 18, 19, 20, 21, 22,
		23, 24, 25, -1, -1, -1, -1, -1
	};

	/// Encode the provided input to the provided output buffer.
	/// @return The number of bytes written, or a calculation for the number
	/// that need to be written, if the provided output buffer size is `0`.
	static int encode(byte[] input, char[] output) {
		if (output.length == 0) return (input.length * 8 + 4) / 5;
		if (input.length == 0) return 0;

		int buffer = 0;
		int remaining = 0;
		int written = 0;

		for (byte b : input) {
			buffer = (buffer << 8) | (b & 0xFF);
			remaining += 8;

			while (remaining >= 5) {
				remaining -= 5;

				output[written] = ALPHABET[(buffer >> remaining) & 0x1F];
				written++;
			}
		}

		if (remaining > 0) {
			remaining = 5 - remaining;

			output[written] = ALPHABET[(buffer << remaining) & 0x1F];
			written++;
		}

		while (written < output.length) {
			output[written] = '=';
			written++;
		}

		return written;
	}

	/// Encode the provided input to a new `char[]`.
	static char[] encode(byte[] input) {
		int size = encode(input, new char[0]);
		char[] buffer = new char[size];

		encode(input, buffer);
		return buffer;
	}

	/// Decodes the provided input into the provided output buffer.
	/// @return The number of bytes written, or a calculation for the number
	/// that need to be written, if the provided output buffer size is `0`.
	static int decode(char[] input, byte[] output) {
		if (output.length == 0) return (input.length * 5) / 8;
		if (input.length == 0) return 0;

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
				output[written] = (byte) ((buffer >> remaining) & 0xFF);
				written++;
			}
		}

		return written;
	}

	/// Decodes the provided input into a new `byte[]`.
	static byte[] decode(char[] input) {
		int size = decode(input, new byte[0]);
		byte[] buffer = new byte[size];

		decode(input, buffer);
		return buffer;
	}
}