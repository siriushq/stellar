package sirius.stellar.security.ksuid;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

import static java.lang.System.arraycopy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayNameGeneration(ReplaceUnderscores.class)
final class KsuidTest {

	@Test
	void identifier_generates_deterministically() {
		var ksuid = Ksuid.builder()
			.clock(() -> 1621627443000L)
			.random(buffer -> {
				byte[] payload = {
					(byte) 0xE1, (byte) 0x93, (byte) 0x3E, (byte) 0x37,
					(byte) 0xF2, (byte) 0x75, (byte) 0x70, (byte) 0x87,
					(byte) 0x63, (byte) 0xAD, (byte) 0xC7, (byte) 0x74,
					(byte) 0x5A, (byte) 0xF5, (byte) 0xE7, (byte) 0xF2
				};
				arraycopy(payload, 0, buffer, 4, 16);
			})
			.build();

		var result = ksuid.identifier();
		var expected = "1srOrx2ZWZBpBUvZwXKQmoEYga2";

		assertThat(result.string())
			.isEqualTo(expected);
		assertThat(result)
			.hasToString(expected);
	}

	@Test
	void identifier_string_parse_as_expected() {
		var ksuid = Ksuid.builder().build();
		var input = "0ujtsYcgvSTl8PAuAdqWYSMnLOv";

		var result = ksuid.identifier(input);

		assertThat(input)
			.isEqualTo(result.string());
		assertThat(result.value())
			.isNotEmpty();
	}

	@Test
	void identifier_array_parse_as_expected() {
		var ksuid = Ksuid.builder().build();
		var input = "0ujtsYcgvSTl8PAuAdqWYSMnLOv".toCharArray();

		var result = ksuid.identifier(input);

		assertThat(result.value())
				.isNotEmpty();
		assertThat(input)
			.isEqualTo(result.value());
	}

	@Test
	void identifier_short_string_parse_throws() {
		var ksuid = Ksuid.builder().build();

		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> ksuid.identifier("too-short"));
	}

	@Test
	void identifier_short_array_parse_throws() {
		var ksuid = Ksuid.builder().build();

		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> ksuid.identifier("too-short".toCharArray()));
	}
}