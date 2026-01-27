package sirius.stellar.serialization.base32;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(ReplaceUnderscores.class)
final class Base32Test {

	@Test
	void encode_and_decode_cycle() {
		var input = "Hello, world!".getBytes();

		var encoded = Base32.encode(input);
		var decoded = Base32.decode(encoded);

		var result = new byte[decoded.remaining()];
		decoded.get(result);

		assertThat(result).isEqualTo(input);
	}

	@Test
	void encode_produces_expected() {
		var input = "Hello, world!".getBytes();
		var encoded = Base32.encode(input);
		assertThat(encoded).isEqualTo("JBSWY3DPFQQHO33SNRSCC");
	}

	@Test
	void decode_produces_expected() {
		var input = "JBSWY3DPFQQHO33SNRSCC===";
		var decoded = Base32.decode(input);

		var result = new byte[decoded.remaining()];
		decoded.get(result);

		assertThat(result).isEqualTo("Hello, world!".getBytes());
	}
}