package sirius.stellar.serialization.base32;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(ReplaceUnderscores.class)
final class Base32Test {

	@Test
	void encode_and_decode_cycle() {
		var input = "Hello, world!".getBytes();

		var encoded = Base32.encode(input);
		var decoded = Base32.decode(encoded);

		assertThat(decoded).isEqualTo(input);
	}

	@Test
	void encode_produces_expected() {
		var input = "Hello, world!".getBytes();
		var encoded = Base32.encode(input);

		var expected = "JBSWY3DPFQQHO33SNRSCC".toCharArray();
		assertThat(encoded).isEqualTo(expected);
	}

	@Test
	void decode_produces_expected() {
		var input = "JBSWY3DPFQQHO33SNRSCC".toCharArray();
		var decoded = Base32.decode(input);

		var expected = "Hello, world!".getBytes();
		assertThat(decoded).isEqualTo(expected);
	}
}