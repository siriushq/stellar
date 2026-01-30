package sirius.stellar.serialization.base62;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(ReplaceUnderscores.class)
final class Base62Test {

	@Test
	void encode_and_decode_cycle() {
		var input = "Hello, world!".getBytes();

		var encoded = Base62.encode(input);
		var decoded = Base62.decode(encoded);

		assertThat(decoded).isEqualTo(input);
	}

	@Test
	void encode_produces_expected() {
		var input = "Hello, world!".getBytes();
		var encoded = Base62.encode(input);

		var expected = "1wJfrzvdbthTq5ANZB".toCharArray();
		assertThat(encoded).isEqualTo(expected);
	}

	@Test
	void decode_produces_expected() {
		var input = "1wJfrzvdbthTq5ANZB".toCharArray();
		var decoded = Base62.decode(input);

		var expected = "Hello, world!".getBytes();
		assertThat(decoded).isEqualTo(expected);
	}

	@Test
	void preserve_leading_zeros() {
		byte[] input = { 0, 0, 0, 0, 42 };

		char[] encoded = Base62.encode(input);
		byte[] decoded = Base62.decode(encoded);

		char[] prefix = { '0', '0', '0', '0' };
		assertThat(encoded).startsWith(prefix);
		assertThat(decoded).isEqualTo(input);
	}
}