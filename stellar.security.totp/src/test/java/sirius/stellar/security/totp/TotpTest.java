package sirius.stellar.security.totp;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import sirius.stellar.serialization.base32.Base32;

import java.util.Map;
import java.util.stream.Stream;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayNameGeneration(ReplaceUnderscores.class)
final class TotpTest {

	/// RFC 6238 test secret, a [Base32]-encoded "12345678901234567890".
	static String SECRET = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ";

	/// Map of epoch seconds, to expected 6-digit codes, with [#SECRET].
	static Map<Long, Integer> VECTORS = Map.of(
		1769663815432L, 314_531,
		1769663833512L, 932_759,
		1769663864125L, 411_395,
		1769663886229L, 813_262,
		1769663963217L, 880_695,
		1769663978311L, 272_014
	);

	@TestFactory
	Stream<DynamicTest> RFC_6238_vectors() {
		return VECTORS.entrySet()
				.stream()
				.map(entry -> {
			long epoch = entry.getKey();
			long expected = entry.getValue();

			return dynamicTest(format("epoch {0} should emit {1}", epoch, expected), () -> {
				var totp = Totp.builder()
						.clock(() -> epoch)
						.step(30_000)
						.build();

				int code = totp.code(SECRET);
				assertThat(code).isEqualTo(expected);
			});
		});
	}

	@TestFactory
	Stream<DynamicTest> clock_drift() {
		long now = 1234567890L;
		long step = 30_000;

		var builder = Totp.builder().step(step);
		return Stream.of(
			dynamicTest("exact match", () -> {
				var totp = builder.clock(() -> now).build();
				int code = totp.code(SECRET);
				assertThat(totp.valid(SECRET, code)).isTrue();
			}),
			dynamicTest("drifting -1 steps", () -> {
				var totp = builder.clock(() -> now - step).build();
				int code = totp.code(SECRET);
				assertThat(totp.valid(SECRET, code)).isTrue();
			}),
			dynamicTest("drifting +1 steps", () -> {
				var totp = builder.clock(() -> now + step).build();
				int code = totp.code(SECRET);
				assertThat(totp.valid(SECRET, code)).isTrue();
			}),
			dynamicTest("fail drifting -3 steps", () -> {
				var totp = builder.clock(() -> now - (step * 3)).build();
				int code = totp.code(SECRET);
				assertThat(totp.valid(SECRET, code)).isFalse();
			})
		);
	}

	@TestFactory
	Stream<DynamicTest> remaining() {
		var epochs = Stream.of(10_000L, 15_000L, 29_999L);
		long step = 30_000;

		var builder = Totp.builder().step(step);
		return epochs.map(elapsed -> {
			long remaining = step - elapsed;
			return dynamicTest(format("{0}ms should leave {1}ms", elapsed, remaining), () -> {
				var totp = builder.clock(() -> elapsed).build();
				assertThat(totp.remaining()).isEqualTo(remaining);
			});
		});
	}
}