package sirius.stellar.serialization.base62;

import java.math.BigInteger;

import static java.lang.System.arraycopy;
import static java.math.BigInteger.ZERO;

/// This interface consists exclusively of static methods for encoding and
/// decoding payloads in a (Base64 sans-symbol) Base62 encoding scheme.
/// The common GMP (GNU Multiple Precision) alphabet is not supported.
///
/// {@snippet lang = "java":
/// byte[] payload = "Hello, world!".getBytes();
///
/// char[] encoded = Base62.encode(payload);
/// println(encoded);
///
/// byte[] decoded = Base32.decode(encoded);
/// println(decoded);
/// }
public interface Base62 {

	/// Number that represents the radix of this encoder (sixty-two).
	BigInteger BASE = BigInteger.valueOf(62);

	/// Lookup table that represents a [Base62] alphabet, A-Z followed by 2-7.
	char[] ALPHABET = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
		'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
		'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
	};

	/// Inverse ASCII lookup table for [Base62] characters, for ASCII 0-127.
	byte[] ASCII = {
		// 0-31 (control characters)
		-1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1,

		// 32-47 (symbols)
		-1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1,

		// 48-57 ('0' - '9')
		0, 1, 2, 3, 4, 5, 6, 7,
		8, 9, -1, -1, -1, -1, -1, -1,

		// 64-79 ('A' - 'O')
		-1, 10, 11, 12, 13, 14, 15, 16,
		17, 18, 19, 20, 21, 22, 23, 24,

		// 80-95 ('P' - 'Z')
		25, 26, 27, 28, 29, 30, 31, 32,
		33, 34, 35, -1, -1, -1, -1, -1,

		// 96-111 ('`', 'a' - 'o')
		-1, 36, 37, 38, 39, 40, 41, 42,
		43, 44, 45, 46, 47, 48, 49, 50,

		// 112-127 ('p' - 'z')
		51, 52, 53, 54, 55, 56, 57, 58,
		59, 60, 61, -1, -1, -1, -1, -1
	};

	/// Encode the provided input to a new `char[]`.
	/// If the input is empty, a new empty array is returned.
	static char[] encode(byte[] input) {
		if (input.length == 0) return new char[0];

		BigInteger value = new BigInteger(1, input);
		StringBuilder builder = new StringBuilder();

		while (value.compareTo(ZERO) > 0) {
			BigInteger[] calculation = value.divideAndRemainder(BASE);
			BigInteger quotient = calculation[0];
			BigInteger remainder = calculation[1];

			builder.insert(0, ALPHABET[remainder.intValue()]);
			value = quotient;
		}

		for (byte b : input) {
			if (b != 0) break;
			builder.insert(0, ALPHABET[0]);
		}

		return builder.toString().toCharArray();
	}

	/// Decodes the provided input into a new `byte[]`.
	/// If the input is empty, a new empty array is returned.
	static byte[] decode(char[] input) {
		if (input.length == 0) return new byte[0];

		int leading = 0;
		for (char c : input) {
			if (c != ALPHABET[0]) break;
			leading++;
		}

		BigInteger value = ZERO;
		for (int i = leading; i < input.length; i++) {
			char c = input[i];

			int digit = (c < 128) ? ASCII[c] : -1;
			if (digit < 0) continue;

			value = value.multiply(BASE)
				.add(BigInteger.valueOf(digit));
		}
		byte[] bytes = value.toByteArray();

		int length = bytes.length;
		int offset = (length > 1) && (bytes[0] == 0) ? 1 : 0;
		int actual = length - offset;

		byte[] result = new byte[leading + actual];
		arraycopy(bytes, offset, result, leading, actual);
		return result;
	}
}