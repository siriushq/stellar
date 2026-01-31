package sirius.stellar.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InaccessibleObjectException;
import java.math.BigInteger;
import java.util.Map;

import static java.lang.System.err;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static sirius.stellar.configuration.Configuration.*;
import static sirius.stellar.configuration.mutator.ConfigurationMutator.reset;

final class EnvironmentConfigurationProviderTest {

	@Test @DisplayName("EnvironmentConfigurationProvider: all property access methods correctly function")
	void access() {
		if (!this.environment(Map.of(
			"EXAMPLE_STRING", "Hello, world!",
			"EXAMPLE_BOOLEAN", "true",
			"EXAMPLE_INTEGER", "123",
			"EXAMPLE_LONG", String.valueOf(Long.MAX_VALUE),
			"EXAMPLE_MAPPED", "9999" + Long.MAX_VALUE
		))) return;

		reset();

		var myString = property("EXAMPLE_STRING");
		var myBoolean = propertyBoolean("EXAMPLE_BOOLEAN");
		var myInteger = propertyInteger("EXAMPLE_INTEGER");
		var myLong = propertyLong("EXAMPLE_LONG");
		var myMapped = propertyAs("EXAMPLE_MAPPED", BigInteger::new);

		assertSoftly(softly -> {
			softly.assertThat(myString).isEqualTo("Hello, world!");
			softly.assertThat(myBoolean).isEqualTo(true);
			softly.assertThat(myInteger).isEqualTo(123);
			softly.assertThat(myLong).isEqualTo(Long.MAX_VALUE);
			softly.assertThat(myMapped).isEqualTo(new BigInteger("9999" + Long.MAX_VALUE));
		});
	}

	/// Append the provided entries to the environment variables of the current JVM with reflection.
	@SuppressWarnings("unchecked")
	private boolean environment(Map<String, String> entries) {
		try {
			var env = System.getenv();

			var field = env.getClass().getDeclaredField("m");
			field.setAccessible(true);

			var writable = (Map<String, String>) field.get(env);
			writable.putAll(entries);
			return true;
		} catch (NoSuchFieldException | IllegalAccessException | InaccessibleObjectException exception) {
			err.printf("Failed to override System#getenv, skipping test (%s)%n", exception);
			return false;
		}
	}
}